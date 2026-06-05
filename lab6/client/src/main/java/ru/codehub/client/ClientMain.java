package ru.codehub.client;

import ru.codehub.client.network.UdpClient;
import ru.codehub.client.util.InputValidator;
import ru.codehub.client.util.MusicBandReader;
import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;

import java.io.IOException;
import java.util.Scanner;

/**
 * Точка входа клиентского приложения.
 * <p>
 * Читает команды из консоли, валидирует их, сериализует в {@link CommandRequest}
 * и отправляет на сервер через {@link UdpClient}. Результат выводится в консоль.
 * </p>
 *
 * <h3>Обязанности клиентского приложения:</h3>
 * <ul>
 *   <li>Чтение команд из консоли.</li>
 *   <li>Валидация вводимых данных.</li>
 *   <li>Сериализация команды и аргументов в объект {@link CommandRequest}.</li>
 *   <li>Отправка запроса на сервер.</li>
 *   <li>Обработка ответа (вывод результата в консоль).</li>
 *   <li>Корректная обработка временной недоступности сервера.</li>
 * </ul>
 *
 * <p>Команда {@code save} клиентом <b>не поддерживается</b> (серверная).<br>
 * Команда {@code exit} завершает работу клиентского приложения.</p>
 *
 * <p>Запуск: {@code java -jar client.jar <host> <port>}</p>
 */
public class ClientMain implements AutoCloseable {

    private final UdpClient udpClient;
    private final Scanner scanner;
    private final MusicBandReader bandReader;

    /**
     * Создаёт клиент с подключением к указанному серверу.
     *
     * @param host хост сервера.
     * @param port UDP-порт сервера.
     * @throws IOException если не удаётся открыть канал.
     */
    public ClientMain(String host, int port) throws IOException {
        this.udpClient = new UdpClient(host, port);
        this.scanner = new Scanner(System.in);
        this.bandReader = new MusicBandReader(scanner);
    }

    /**
     * Запускает интерактивный цикл чтения и отправки команд.
     */
    public void run() {
        System.out.println("Music Band Collection Client");
        System.out.println("Type 'help' for available commands, 'exit' to quit.");
        System.out.println();

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];

            // Команда exit завершает клиент локально, без обращения к серверу
            if ("exit".equals(commandName)) {
                System.out.println("Goodbye!");
                break;
            }

            // Команда save запрещена для клиента (только серверная команда)
            if ("save".equals(commandName)) {
                System.out.println("[Client] Command 'save' is not available on the client side.");
                System.out.println("[Client] Use the server console to execute 'save'.");
                continue;
            }

            // Валидация аргументов и построение запроса
            CommandRequest request = buildRequest(commandName, args);
            if (request == null) continue; // ошибка валидации уже выведена

            // Отправка и вывод результата
            CommandResponse response = udpClient.send(request);
            if (!response.isSuccess()) {
                // Проверяем, связана ли ошибка с недоступностью сервера
                String msg = response.getMessage();
                if (msg != null && (msg.contains("unavailable") || msg.contains("timeout")
                        || msg.contains("Send error") || msg.contains("attempts"))) {
                    System.out.println("[Client] " + msg);
                    System.out.println("[Client] The server may be temporarily unavailable. Please try again later.");
                } else {
                    System.out.println("[ERROR] " + msg);
                }
            } else {
                System.out.println(response.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * Строит объект {@link CommandRequest} для указанной команды.
     * Для команд, требующих ввода объекта ({@code add}, {@code update}, {@code add_if_max}),
     * запускает интерактивный ввод через {@link MusicBandReader}.
     *
     * @param commandName название команды.
     * @param args        аргументы из командной строки.
     * @return готовый запрос или {@code null} при ошибке валидации.
     */
    private CommandRequest buildRequest(String commandName, String[] args) {
        switch (commandName) {
            case "add":
            case "add_if_max": {
                System.out.println("Enter MusicBand fields:");
                MusicBand band = bandReader.read();
                return new CommandRequest(commandName, band);
            }

            case "update": {
                if (args.length == 0 || !InputValidator.isPositiveLong(args[0])) {
                    System.out.println("[Client] Usage: update <id>");
                    return null;
                }
                System.out.println("Enter new MusicBand fields:");
                MusicBand band = bandReader.read();
                return new CommandRequest(commandName, band, args[0]);
            }

            case "remove_by_id": {
                if (args.length == 0 || !InputValidator.isPositiveLong(args[0])) {
                    System.out.println("[Client] Usage: remove_by_id <id>");
                    return null;
                }
                return new CommandRequest(commandName, args[0]);
            }

            case "filter_contains_name": {
                if (args.length == 0 || !InputValidator.isNotBlank(args[0])) {
                    System.out.println("[Client] Usage: filter_contains_name <substring>");
                    return null;
                }
                return new CommandRequest(commandName, args[0]);
            }

            default:
                // Все остальные команды не требуют дополнительных аргументов
                return new CommandRequest(commandName, args);
        }
    }

    /**
     * Точка старта JVM.
     *
     * @param args args[0] — хост сервера, args[1] — UDP-порт.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar client.jar <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port: " + args[1]);
            System.exit(1);
            return;
        }

        try (ClientMain client = new ClientMain(host, port)) {
            client.run();
        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Закрывает ресурсы клиента (реализует {@link AutoCloseable}).
     */
    public void close() {
        udpClient.close();
    }
}
