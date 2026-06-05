package ru.codehub.server;

import ru.codehub.common.model.MusicBand;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.collection.MusicBandCollectionManager;
import ru.codehub.server.command.CommandDispatcher;
import ru.codehub.server.command.impl.*;
import ru.codehub.server.io.CollectionReader;
import ru.codehub.server.io.CollectionWriter;
import ru.codehub.server.io.CsvCollectionReader;
import ru.codehub.server.io.CsvCollectionWriter;
import ru.codehub.server.network.UdpServer;
import ru.codehub.server.util.IdGenerator;

import java.io.IOException;
import java.util.Collection;

/**
 * Точка входа серверного приложения.
 * <p>
 * Выполняет:
 * <ol>
 *   <li>Загрузку коллекции из CSV-файла.</li>
 *   <li>Инициализацию менеджера коллекции и регистрацию всех обработчиков команд.</li>
 *   <li>Запуск {@link ServerConsole} в отдельном потоке-демоне для обработки
 *       серверных команд ({@code save}, {@code exit}).</li>
 *   <li>Запуск UDP-сервера на указанном порту (основной поток).</li>
 *   <li>Автосохранение коллекции при завершении (shutdown hook).</li>
 * </ol>
 * </p>
 *
 * <p>Сервер работает в <b>однопоточном режиме</b> для сетевых операций.</p>
 * <p>Запуск: {@code java -jar server.jar <port> <csv_file>}</p>
 */
public class ServerMain {

    /**
     * Точка старта JVM.
     *
     * @param args args[0] — порт UDP, args[1] — путь к CSV-файлу с коллекцией.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar server.jar <port> <csv_file>");
            System.exit(1);
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port: " + args[0]);
            System.exit(1);
            return;
        }
        String filePath = args[1];

        // ---- Инициализация компонентов ----
        IdGenerator idGenerator = new IdGenerator();
        CollectionManager manager = new MusicBandCollectionManager(idGenerator);
        CollectionReader reader = new CsvCollectionReader();
        CollectionWriter writer = new CsvCollectionWriter();

        // ---- Загрузка коллекции из файла ----
        try {
            Collection<MusicBand> bands = reader.read(filePath);
            manager.loadCollection(bands);
            System.out.println("[Server] Loaded " + manager.size() + " elements from " + filePath);
        } catch (IOException e) {
            System.out.println("[Server] Warning: could not load collection: " + e.getMessage());
            System.out.println("[Server] Starting with empty collection.");
        }

        // ---- Регистрация обработчиков команд (Command pattern) ----
        CommandDispatcher dispatcher = new CommandDispatcher();
        dispatcher.register(new HelpHandler());
        dispatcher.register(new InfoHandler(manager));
        dispatcher.register(new ShowHandler(manager));
        dispatcher.register(new AddHandler(manager));
        dispatcher.register(new UpdateHandler(manager));
        dispatcher.register(new RemoveByIdHandler(manager));
        dispatcher.register(new ClearHandler(manager));
        dispatcher.register(new RemoveHeadHandler(manager));
        dispatcher.register(new AddIfMaxHandler(manager));
        dispatcher.register(new MinByCreationDateHandler(manager));
        dispatcher.register(new GroupCountingByNameHandler(manager));
        dispatcher.register(new FilterContainsNameHandler(manager));
        // save — ТОЛЬКО серверная команда: клиент не может её отправить (требование задания)
        dispatcher.register(new SaveHandler(manager, writer, filePath));

        // ---- Создаём UDP-сервер (запустим после консоли) ----
        UdpServer server = new UdpServer(port, dispatcher);

        // ---- Автосохранение при завершении работы сервера ----
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Server] Saving collection before shutdown...");
            try {
                writer.write(manager.getCollection(), filePath);
                System.out.println("[Server] Collection saved to " + filePath);
            } catch (IOException e) {
                System.err.println("[Server] Failed to save: " + e.getMessage());
            }
        }));

        // ---- Запуск серверной консоли в потоке-демоне ----
        // Консоль обрабатывает серверные команды: save и exit.
        // save доступна ТОЛЬКО здесь — клиент отправить её не может.
        ServerConsole console = new ServerConsole(manager, writer, filePath, () -> {
            server.stop();
            System.exit(0);
        });
        Thread consoleThread = new Thread(console, "server-console");
        consoleThread.setDaemon(true); // не мешает завершению JVM
        consoleThread.start();

        // ---- Запуск UDP-сервера (основной поток блокируется здесь) ----
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("[Server] Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}
