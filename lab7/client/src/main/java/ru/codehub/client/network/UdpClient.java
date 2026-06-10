package ru.codehub.client.network;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.common.network.Serializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Сетевой модуль клиента.
 * <p>
 * Отправляет сериализованные {@link CommandRequest} на сервер по UDP
 * и ожидает {@link CommandResponse} с использованием {@link DatagramChannel}
 * в <b>неблокирующем режиме</b> и {@link Selector} для ожидания ответа.
 * </p>
 *
 * <h3>Требования задания:</h3>
 * <ul>
 *   <li>Обмен данными по протоколу <b>UDP</b>.</li>
 *   <li>Для обмена на клиенте используется <b>сетевой канал</b> ({@link DatagramChannel}).</li>
 *   <li>Сетевые каналы используются в <b>неблокирующем режиме</b>.</li>
 *   <li>Объекты передаются в <b>сериализованном виде</b>.</li>
 *   <li>Клиент корректно обрабатывает <b>временную недоступность сервера</b>.</li>
 * </ul>
 */
public class UdpClient implements AutoCloseable {

    /** Размер буфера приёма (должен соответствовать серверному). */
    private static final int BUFFER_SIZE = 65507;

    /** Таймаут ожидания ответа от сервера (мс). */
    private static final int TIMEOUT_MS = 5000;

    /** Количество попыток переотправки при недоступности сервера. */
    private static final int MAX_RETRIES = 3;

    private final InetSocketAddress serverAddress;
    private DatagramChannel channel;
    private Selector selector;

    /**
     * Создаёт клиент и подключает {@link DatagramChannel} к адресу сервера.
     *
     * @param host хост сервера.
     * @param port UDP-порт сервера.
     * @throws IOException если не удаётся открыть канал.
     */
    public UdpClient(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);
        openChannel();
    }

    /**
     * Открывает (или переоткрывает) DatagramChannel и Selector.
     * Вызывается при инициализации и при восстановлении после ошибки.
     */
    private void openChannel() throws IOException {
        if (selector != null) {
            try { selector.close(); } catch (IOException ignored) {}
        }
        if (channel != null) {
            try { channel.close(); } catch (IOException ignored) {}
        }

        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.connect(serverAddress);

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Отправляет запрос на сервер и ожидает ответ.
     * <p>
     * При недоступности сервера выполняет до {@value MAX_RETRIES} попыток,
     * выводя предупреждение. Если все попытки исчерпаны — возвращает ответ с ошибкой.
     * Это обеспечивает корректную обработку <b>временной недоступности сервера</b>.
     * </p>
     *
     * @param request запрос для отправки.
     * @return ответ от сервера или ответ-ошибка при недоступности.
     */
    public CommandResponse send(CommandRequest request) {
        byte[] data;
        try {
            data = Serializer.serialize(request);
        } catch (IOException e) {
            return CommandResponse.error("Serialization error: " + e.getMessage());
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            // При повторной попытке переоткрываем канал, чтобы сбросить
            // накопленное состояние (буферы, старые пакеты) — это критично для UDP
            if (attempt > 1) {
                System.out.println("[Client] Server unavailable, retry " + attempt + "/" + MAX_RETRIES + "...");
                try {
                    Thread.sleep(1000L * (attempt - 1)); // экспоненциальная задержка
                    openChannel();
                } catch (IOException | InterruptedException e) {
                    return CommandResponse.error("Connection error: " + e.getMessage());
                }
            }

            // --- Отправка ---
            try {
                ByteBuffer sendBuf = ByteBuffer.wrap(data);
                channel.write(sendBuf);
            } catch (IOException e) {
                if (attempt == MAX_RETRIES) {
                    return CommandResponse.error("Send error: " + e.getMessage());
                }
                continue;
            }

            // --- Ожидание ответа через Selector (неблокирующий режим) ---
            try {
                // Очищаем набор выбранных ключей перед select(),
                // чтобы не получить "phantom readability" от предыдущего вызова
                selector.selectedKeys().clear();

                int ready = selector.select(TIMEOUT_MS);
                if (ready == 0) {
                    // Таймаут — сервер временно недоступен, попробуем ещё раз
                    if (attempt == MAX_RETRIES) {
                        return CommandResponse.error(
                                "Server is unavailable. Tried " + MAX_RETRIES + " times. Please try again later.");
                    }
                    continue;
                }

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isReadable()) {
                        ByteBuffer recvBuf = ByteBuffer.allocate(BUFFER_SIZE);
                        channel.read(recvBuf);
                        recvBuf.flip();
                        byte[] recvData = new byte[recvBuf.remaining()];
                        recvBuf.get(recvData);

                        try {
                            Object obj = Serializer.deserialize(recvData);
                            if (obj instanceof CommandResponse) {
                                return (CommandResponse) obj;
                            } else {
                                return CommandResponse.error("Unexpected response type from server");
                            }
                        } catch (ClassNotFoundException e) {
                            return CommandResponse.error(
                                    "Deserialization error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
                        }
                    }
                }
            } catch (IOException e) {
                if (attempt == MAX_RETRIES) {
                    String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                    return CommandResponse.error("Receive error: " + msg);
                }
            }
        }

        return CommandResponse.error("No response received after " + MAX_RETRIES + " attempts.");
    }

    /**
     * Закрывает {@link DatagramChannel} и {@link Selector}.
     */
    @Override
    public void close() {
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
        } catch (IOException e) {
            System.err.println("[Client] Error closing connection: " + e.getMessage());
        }
    }
}
