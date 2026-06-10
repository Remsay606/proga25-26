package ru.codehub.server.network;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.common.network.Serializer;
import ru.codehub.server.command.CommandDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Многопоточный UDP-сервер.
 *
 * <h3>Многопоточная обработка запросов (требования лаб. 7):</h3>
 * <ul>
 *   <li><b>Чтение запросов</b> — {@code Fixed thread pool}
 *       ({@link Executors#newFixedThreadPool(int)}): принятая датаграмма
 *       передаётся в пул для десериализации.</li>
 *   <li><b>Обработка запроса</b> — создание <b>нового потока</b>
 *       ({@code new Thread(...)}) на каждую команду.</li>
 *   <li><b>Отправка ответа</b> — {@link ForkJoinPool}.</li>
 * </ul>
 *
 * <h3>Сетевой уровень:</h3>
 * <ul>
 *   <li>Протокол: <b>UDP</b> (датаграммы на стороне сервера).</li>
 *   <li>Канал: {@link DatagramChannel} в <b>неблокирующем режиме</b>.</li>
 *   <li>Мультиплексирование приёма: {@link Selector}.</li>
 *   <li>Объекты передаются в <b>сериализованном виде</b>.</li>
 * </ul>
 *
 * <p>Сам цикл приёма ({@code select} + {@code receive}) однопоточный;
 * параллелится последующая обработка. Доступ к каналу при отправке
 * синхронизирован, так как {@link DatagramChannel#send} может вызываться
 * из разных потоков.</p>
 */
public class UdpServer {

    /** Максимальный размер UDP-датаграммы. */
    private static final int BUFFER_SIZE = 65507;

    private final int port;
    private final CommandDispatcher dispatcher;
    private volatile boolean running = false;

    private DatagramChannel channel;
    private Selector selector;

    /** Пул потоков для чтения (десериализации) запросов. */
    private final ExecutorService readPool = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors()));

    /** Пул для отправки ответов. */
    private final ForkJoinPool sendPool = new ForkJoinPool();

    /** Монитор для синхронизации отправки в канал из разных потоков. */
    private final Object sendLock = new Object();

    /**
     * @param port       UDP-порт.
     * @param dispatcher диспетчер команд.
     */
    public UdpServer(int port, CommandDispatcher dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }

    /**
     * Запускает сервер: открывает неблокирующий {@link DatagramChannel},
     * регистрирует в {@link Selector} и входит в цикл приёма датаграмм.
     *
     * @throws IOException если не удалось открыть/привязать сокет.
     */
    public void start() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        running = true;
        System.out.println("[Server] Multithreaded UDP server started on port " + port);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (running) {
            int ready = selector.select(500);
            if (ready == 0) continue;

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (key.isReadable()) {
                    receiveOne(buffer);
                }
            }
        }
    }

    /**
     * Принимает одну датаграмму и передаёт её данные в пул чтения.
     *
     * @param buffer переиспользуемый буфер приёма.
     */
    private void receiveOne(ByteBuffer buffer) {
        buffer.clear();
        SocketAddress clientAddress;
        try {
            clientAddress = channel.receive(buffer);
        } catch (IOException e) {
            System.err.println("[Server] Receive error: " + e.getMessage());
            return;
        }
        if (clientAddress == null) return;

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        // --- Чтение (десериализация) запроса: Fixed thread pool ---
        readPool.submit(() -> handleRead(data, clientAddress));
    }

    /**
     * Десериализует запрос (в потоке readPool) и запускает обработку
     * в отдельном новом потоке.
     *
     * @param data          сырые байты датаграммы.
     * @param clientAddress адрес клиента.
     */
    private void handleRead(byte[] data, SocketAddress clientAddress) {
        CommandRequest request;
        try {
            Object obj = Serializer.deserialize(data);
            if (!(obj instanceof CommandRequest)) {
                sendResponseAsync(CommandResponse.error("Invalid request type"), clientAddress);
                return;
            }
            request = (CommandRequest) obj;
        } catch (Exception e) {
            sendResponseAsync(CommandResponse.error("Invalid request format"), clientAddress);
            return;
        }

        String who = request.getUser() != null ? request.getUser().getLogin() : "unknown";
        System.out.println("[Server] Command '" + request.getCommandName()
                + "' from " + clientAddress + " (user=" + who + ")");

        // --- Обработка запроса: новый поток на каждую команду ---
        Thread worker = new Thread(() -> {
            CommandResponse response = dispatcher.dispatch(request);
            sendResponseAsync(response, clientAddress);
        }, "cmd-worker");
        worker.start();
    }

    /**
     * Отправляет ответ клиенту через {@link ForkJoinPool}.
     *
     * @param response      ответ сервера.
     * @param clientAddress адрес клиента.
     */
    private void sendResponseAsync(CommandResponse response, SocketAddress clientAddress) {
        sendPool.submit(() -> {
            try {
                byte[] data = Serializer.serialize(response);
                ByteBuffer buf = ByteBuffer.wrap(data);
                // send() может вызываться из разных потоков — синхронизируем
                synchronized (sendLock) {
                    channel.send(buf, clientAddress);
                }
            } catch (IOException e) {
                System.err.println("[Server] Send error: " + e.getMessage());
            }
        });
    }

    /**
     * Останавливает сервер и освобождает ресурсы (каналы и пулы потоков).
     */
    public void stop() {
        running = false;
        readPool.shutdown();
        sendPool.shutdown();
        try {
            readPool.awaitTermination(2, TimeUnit.SECONDS);
            sendPool.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
        } catch (IOException e) {
            System.err.println("[Server] Error closing resources: " + e.getMessage());
        }
        System.out.println("[Server] Stopped.");
    }
}
