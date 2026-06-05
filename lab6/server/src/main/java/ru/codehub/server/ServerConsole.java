package ru.codehub.server;

import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.io.CollectionWriter;

import java.io.IOException;
import java.util.Scanner;

/**
 * Серверная консоль — выполняется в отдельном потоке и позволяет администратору
 * вводить команды прямо на сервере.
 *
 * <p>Доступные команды серверной консоли:</p>
 * <ul>
 *   <li>{@code save} — принудительное сохранение коллекции в файл.
 *       Это <b>серверная команда</b>: клиент отправить её не может (требование задания).</li>
 *   <li>{@code exit} — корректное завершение работы сервера (с автосохранением).</li>
 * </ul>
 *
 * <p>Сервер работает в <b>однопоточном режиме</b> для сетевых операций,
 * но консоль выделена в отдельный поток-демон, чтобы не блокировать
 * основной цикл приёма UDP-датаграмм.</p>
 */
public class ServerConsole implements Runnable {

    private final CollectionManager manager;
    private final CollectionWriter writer;
    private final String filePath;
    private final Runnable stopServerCallback;

    /**
     * @param manager            менеджер коллекции.
     * @param writer             объект записи в файл.
     * @param filePath           путь к файлу сохранения.
     * @param stopServerCallback колбэк для остановки UDP-сервера (вызывается при {@code exit}).
     */
    public ServerConsole(CollectionManager manager,
                         CollectionWriter writer,
                         String filePath,
                         Runnable stopServerCallback) {
        this.manager = manager;
        this.writer = writer;
        this.filePath = filePath;
        this.stopServerCallback = stopServerCallback;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Server] Console ready. Available commands: save, exit");

        while (!Thread.currentThread().isInterrupted()) {
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim().toLowerCase();

            switch (line) {
                case "save":
                    // Серверная команда save: клиент не может её отправить
                    try {
                        writer.write(manager.getCollection(), filePath);
                        System.out.println("[Server] Collection saved to " + filePath);
                    } catch (IOException e) {
                        System.err.println("[Server] Save failed: " + e.getMessage());
                    }
                    break;

                case "exit":
                    System.out.println("[Server] Shutting down...");
                    stopServerCallback.run();
                    return;

                case "":
                    break;

                default:
                    System.out.println("[Server] Unknown server command: '" + line
                            + "'. Available: save, exit");
            }
        }
    }
}
