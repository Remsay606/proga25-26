package ru.codehub.server;

import java.util.Scanner;

/**
 * Серверная консоль — выполняется в отдельном потоке-демоне и позволяет
 * администратору завершить работу сервера командой {@code exit}.
 * <p>
 * <b>Лаб. 7:</b> коллекция хранится в БД, поэтому отдельная команда сохранения
 * в файл больше не нужна. Консоль оставлена для корректной остановки сервера
 * (закрытие соединения с БД, освобождение сетевых ресурсов).
 * </p>
 */
public class ServerConsole implements Runnable {

    private final Runnable stopServerCallback;

    /**
     * @param stopServerCallback колбэк остановки сервера.
     */
    public ServerConsole(Runnable stopServerCallback) {
        this.stopServerCallback = stopServerCallback;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Server] Console ready. Available commands: exit");

        while (!Thread.currentThread().isInterrupted()) {
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim().toLowerCase();
            switch (line) {
                case "exit":
                    System.out.println("[Server] Shutting down...");
                    stopServerCallback.run();
                    return;
                case "":
                    break;
                default:
                    System.out.println("[Server] Unknown command: '" + line + "'. Available: exit");
            }
        }
    }
}
