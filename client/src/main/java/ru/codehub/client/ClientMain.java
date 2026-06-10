package ru.codehub.client;

import ru.codehub.client.network.UdpClient;
import ru.codehub.client.util.InputValidator;
import ru.codehub.client.util.MusicBandReader;
import ru.codehub.common.model.MusicBand;
import ru.codehub.common.model.User;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;

import java.io.IOException;
import java.util.Scanner;

/**
 * Клиентское приложение с авторизацией и ролевым доступом.
 *
 * <p>После входа команда {@code exit} возвращает в меню авторизации.
 * Полный выход — пункт [3] в меню.</p>
 *
 * <p>Команды {@code promote_user} и {@code demote_admin} доступны только ADMIN
 * (сервер вернёт ошибку если отправит USER).</p>
 */
public class ClientMain implements AutoCloseable {

    private final UdpClient udpClient;
    private final Scanner scanner;
    private final MusicBandReader bandReader;
    private User currentUser;

    public ClientMain(String host, int port) throws IOException {
        this.udpClient  = new UdpClient(host, port);
        this.scanner    = new Scanner(System.in);
        this.bandReader = new MusicBandReader(scanner);
    }

    public void run() {
        System.out.println("Music Band Collection Client");
        System.out.println();

        while (true) {
            if (!authLoop()) {
                System.out.println("Goodbye!");
                return;
            }
            System.out.println();
            System.out.println("Logged in as '" + currentUser.getLogin() + "'.");
            System.out.println("Type 'help' to see commands, 'exit' to log out.");
            System.out.println();

            commandLoop();

            System.out.println();
            System.out.println("Logged out. Returning to main menu...");
            System.out.println();
            currentUser = null;
        }
    }

    private boolean authLoop() {
        while (true) {
            System.out.println("Choose: [1] login  [2] register  [3] exit");
            System.out.print("> ");
            if (!scanner.hasNextLine()) return false;
            String choice = scanner.nextLine().trim();

            if (choice.equals("3") || choice.equalsIgnoreCase("exit")) return false;
            if (!choice.equals("1") && !choice.equals("2")) {
                System.out.println("Invalid choice.");
                continue;
            }

            String login    = readNonEmpty("Login: ");
            String password = readNonEmpty("Password: ");
            User candidate  = new User(login, password);

            String cmd = choice.equals("1") ? "login" : "register";
            CommandRequest req = new CommandRequest(cmd);
            req.setUser(candidate);

            CommandResponse resp = udpClient.send(req);
            if (resp.isSuccess()) {
                System.out.println(resp.getMessage());
                currentUser = candidate;
                return true;
            } else {
                System.out.println("[ERROR] " + resp.getMessage());
                System.out.println();
            }
        }
    }

    private void commandLoop() {
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmd  = parts[0].toLowerCase();
            String[] args = (parts.length > 1) ? parts[1].split("\\s+") : new String[0];

            if ("exit".equals(cmd)) { System.out.println("Logged out."); break; }

            if ("save".equals(cmd)) {
                System.out.println("[Client] Command 'save' is not available (data is in the database).");
                continue;
            }

            CommandRequest request = buildRequest(cmd, args);
            if (request == null) continue;
            request.setUser(currentUser);

            CommandResponse response = udpClient.send(request);
            if (!response.isSuccess()) {
                String msg = response.getMessage();
                if (msg != null && (msg.contains("unavailable") || msg.contains("timeout")
                        || msg.contains("Send error") || msg.contains("attempts"))) {
                    System.out.println("[Client] " + msg);
                    System.out.println("[Client] Server may be temporarily unavailable.");
                } else {
                    System.out.println("[ERROR] " + msg);
                }
            } else {
                System.out.println(response.getMessage());
            }
            System.out.println();
        }
    }

    private CommandRequest buildRequest(String cmd, String[] args) {
        switch (cmd) {
            case "add":
            case "add_if_min": {
                System.out.println("Enter MusicBand fields:");
                MusicBand band = bandReader.read();
                return new CommandRequest(cmd, band);
            }
            case "update": {
                if (args.length == 0 || !InputValidator.isPositiveLong(args[0])) {
                    System.out.println("[Client] Usage: update <id>");
                    return null;
                }
                System.out.println("Enter new MusicBand fields:");
                MusicBand band = bandReader.read();
                return new CommandRequest(cmd, band, args[0]);
            }
            case "remove_by_id": {
                if (args.length == 0 || !InputValidator.isPositiveLong(args[0])) {
                    System.out.println("[Client] Usage: remove_by_id <id>");
                    return null;
                }
                return new CommandRequest(cmd, args[0]);
            }
            case "filter_contains_name": {
                if (args.length == 0 || !InputValidator.isNotBlank(args[0])) {
                    System.out.println("[Client] Usage: filter_contains_name <substring>");
                    return null;
                }
                return new CommandRequest(cmd, args[0]);
            }
            case "promote_user": {
                if (args.length == 0 || !InputValidator.isNotBlank(args[0])) {
                    System.out.println("[Client] Usage: promote_user <login>");
                    return null;
                }
                return new CommandRequest(cmd, args[0]);
            }
            case "demote_admin": {
                if (args.length == 0 || !InputValidator.isNotBlank(args[0])) {
                    System.out.println("[Client] Usage: demote_admin <login>");
                    return null;
                }
                return new CommandRequest(cmd, args[0]);
            }
            default:
                return new CommandRequest(cmd, args);
        }
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) return "";
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  Value cannot be empty.");
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar client.jar <host> <port>");
            System.exit(1);
        }
        int port;
        try { port = Integer.parseInt(args[1]); }
        catch (NumberFormatException e) {
            System.err.println("Invalid port: " + args[1]);
            System.exit(1); return;
        }
        try (ClientMain client = new ClientMain(args[0], port)) {
            client.run();
        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override public void close() { udpClient.close(); }
}
