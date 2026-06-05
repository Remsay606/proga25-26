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
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

/**
 * Сетевой модуль сервера — принимает UDP-датаграммы, десериализует запросы,
 * передаёт их в {@link CommandDispatcher}, сериализует ответ и отправляет клиенту.
 *
 * <h3>Технические детали:</h3>
 * <ul>
 *   <li>Протокол: <b>UDP</b> (DatagramSocket на уровне ОС).</li>
 *   <li>Канал: {@link DatagramChannel} в <b>неблокирующем режиме</b> ({@code configureBlocking(false)}).</li>
 *   <li>Мультиплексирование: {@link Selector} — позволяет ждать входящих пакетов без блокировки потока.</li>
 *   <li>Объекты передаются в <b>сериализованном виде</b> через {@link Serializer}.</li>
 *   <li>Сервер работает в <b>однопоточном режиме</b>.</li>
 * </ul>
 *
 * <p>Паттерн: <b>Facade</b> — скрывает детали работы с NIO от остальной части сервера.</p>
 */
public class UdpServer {

    /** Максимальный размер UDP-датаграммы (байт). */
    private static final int BUFFER_SIZE = 65507;

    private final int port;
    private final CommandDispatcher dispatcher;
    private volatile boolean running = false;

    private DatagramChannel channel;
    private Selector selector;

    /**
     * Создаёт сервер на указанном порту.
     *
     * @param port       номер UDP-порта для прослушивания.
     * @param dispatcher диспетчер команд для обработки запросов.
     */
    public UdpServer(int port, CommandDispatcher dispatcher) {
        this.port = port;
        this.dispatcher = dispatcher;
    }

    /**
     * Запускает сервер: открывает {@link DatagramChannel}, переключает его
     * в <b>неблокирующий режим</b>, регистрирует в {@link Selector} и входит в цикл обработки.
     *
     * @throws IOException если не удаётся открыть сокет или привязаться к порту.
     */
    public void start() throws IOException {
        // Открываем канал и переводим в неблокирующий режим (Selector требует этого)
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));

        // Selector позволяет отслеживать готовность канала без блокировки
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        running = true;
        System.out.println("[Server] UDP server started on port " + port);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (running) {
            // select() с таймаутом — не блокирует навсегда, даёт шанс проверить running
            int ready = selector.select(500);
            if (ready == 0) continue;

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isReadable()) {
                    handleIncoming(buffer);
                }
            }
        }
    }

    /**
     * Читает одну датаграмму из канала, десериализует {@link CommandRequest},
     * вызывает диспетчер и отправляет {@link CommandResponse} обратно клиенту.
     *
     * @param buffer буфер для приёма данных (переиспользуется).
     */
    private void handleIncoming(ByteBuffer buffer) {
        buffer.clear();
        SocketAddress clientAddress;
        try {
            // receive() — неблокирующий, возвращает null если нет данных
            clientAddress = channel.receive(buffer);
        } catch (IOException e) {
            System.err.println("[Server] Receive error: " + e.getMessage());
            return;
        }
        if (clientAddress == null) return;

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        CommandRequest request;
        try {
            request = (CommandRequest) Serializer.deserialize(data);
        } catch (Exception e) {
            System.err.println("[Server] Deserialization error: " + e.getMessage());
            sendResponse(CommandResponse.error("Invalid request format"), clientAddress);
            return;
        }

        System.out.println("[Server] Received command: " + request.getCommandName()
                + " from " + clientAddress);

        CommandResponse response = dispatcher.dispatch(request);
        sendResponse(response, clientAddress);
    }

    /**
     * Сериализует {@link CommandResponse} и отправляет его по UDP клиенту.
     *
     * @param response      ответ сервера.
     * @param clientAddress адрес клиента.
     */
    private void sendResponse(CommandResponse response, SocketAddress clientAddress) {
        try {
            byte[] data = Serializer.serialize(response);
            ByteBuffer buf = ByteBuffer.wrap(data);
            channel.send(buf, clientAddress);
        } catch (IOException e) {
            System.err.println("[Server] Send error: " + e.getMessage());
        }
    }

    /**
     * Останавливает сервер и освобождает сетевые ресурсы.
     */
    public void stop() {
        running = false;
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
        } catch (IOException e) {
            System.err.println("[Server] Error closing resources: " + e.getMessage());
        }
        System.out.println("[Server] Stopped.");
    }
}
