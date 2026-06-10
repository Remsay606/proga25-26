package ru.codehub.server;

import ru.codehub.server.auth.AuthService;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.collection.MusicBandCollectionManager;
import ru.codehub.server.command.CommandDispatcher;
import ru.codehub.server.command.impl.*;
import ru.codehub.server.db.DatabaseManager;
import ru.codehub.server.db.DbConfig;
import ru.codehub.server.db.MusicBandDao;
import ru.codehub.server.db.UserDao;
import ru.codehub.server.network.UdpServer;

import java.io.IOException;

/**
 * Точка входа серверного приложения (лаб. 7).
 *
 * <p>Запуск: {@code java -jar server.jar <port> <db_config_file>}</p>
 *
 * <p>При старте создаётся администратор из конфига ({@code admin.login} /
 * {@code admin.password}) если его ещё нет в БД.</p>
 */
public class ServerMain {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar server.jar <port> <db_config_file>");
            System.exit(1);
        }

        int port;
        try { port = Integer.parseInt(args[0]); }
        catch (NumberFormatException e) {
            System.err.println("Invalid port: " + args[0]);
            System.exit(1); return;
        }

        // ---- 1. Конфигурация ----
        DbConfig dbConfig;
        try { dbConfig = DbConfig.fromFile(args[1]); }
        catch (IOException e) {
            System.err.println("[Server] Failed to load config: " + e.getMessage());
            System.exit(1); return;
        }

        // ---- 2. База данных ----
        DatabaseManager db = new DatabaseManager(dbConfig);
        try { db.connect(); System.out.println("[Server] Connected to database."); }
        catch (Exception e) {
            System.err.println("[Server] DB connection failed: " + e.getMessage());
            System.exit(1); return;
        }

        MusicBandDao bandDao = new MusicBandDao(db);
        UserDao      userDao = new UserDao(db);

        // AuthService создаёт дефолтного admin при инициализации
        AuthService authService = new AuthService(userDao, dbConfig);
        CollectionManager manager = new MusicBandCollectionManager(bandDao);

        // ---- 3. Загрузка коллекции ----
        try {
            manager.loadFromDatabase();
            System.out.println("[Server] Loaded " + manager.size() + " elements from database.");
        } catch (Exception e) {
            System.err.println("[Server] Failed to load collection: " + e.getMessage());
        }

        // ---- 4. Обработчики команд ----
        CommandDispatcher dispatcher = new CommandDispatcher(authService);
        dispatcher.register(new HelpHandler());
        dispatcher.register(new InfoHandler(manager));
        dispatcher.register(new ShowHandler(manager));
        dispatcher.register(new AddHandler(manager));
        dispatcher.register(new UpdateHandler(manager));
        dispatcher.register(new RemoveByIdHandler(manager));
        dispatcher.register(new ClearHandler(manager));
        dispatcher.register(new RemoveHeadHandler(manager));
        dispatcher.register(new AddIfMinHandler(manager));
        dispatcher.register(new MinByCreationDateHandler(manager));
        dispatcher.register(new GroupCountingByNameHandler(manager));
        dispatcher.register(new FilterContainsNameHandler(manager));
        // Admin-команды
        dispatcher.register(new PromoteUserHandler(authService));
        dispatcher.register(new DemoteAdminHandler(authService));

        UdpServer server = new UdpServer(port, dispatcher);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Server] Shutting down...");
            db.close();
        }));

        // ---- 5. Серверная консоль ----
        ServerConsole console = new ServerConsole(() -> {
            server.stop(); db.close(); System.exit(0);
        });
        Thread consoleThread = new Thread(console, "server-console");
        consoleThread.setDaemon(true);
        consoleThread.start();

        // ---- 6. UDP-сервер ----
        try { server.start(); }
        catch (IOException e) {
            System.err.println("[Server] Fatal: " + e.getMessage());
            db.close(); System.exit(1);
        }
    }
}
