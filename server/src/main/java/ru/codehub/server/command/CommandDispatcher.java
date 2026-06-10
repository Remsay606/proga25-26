package ru.codehub.server.command;

import ru.codehub.common.model.User;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.auth.AuthService;

import java.util.HashMap;
import java.util.Map;

/**
 * Диспетчер команд с проверкой авторизации и передачей роли в хендлеры.
 *
 * <p>Перед маршрутизацией:
 * <ol>
 *   <li>register/login — без предварительной авторизации.</li>
 *   <li>Все остальные — аутентификация + получение роли.</li>
 *   <li>Команды promote_user/demote_admin — только для ADMIN.</li>
 * </ol>
 * </p>
 */
public class CommandDispatcher {

    private final Map<String, ServerCommandHandler> handlers = new HashMap<>();
    private final AuthService authService;

    public CommandDispatcher(AuthService authService) {
        this.authService = authService;
    }

    public void register(ServerCommandHandler handler) {
        handlers.put(handler.getCommandName(), handler);
    }

    public CommandResponse dispatch(CommandRequest request) {
        if (request == null || request.getCommandName() == null) {
            return CommandResponse.error("Empty request");
        }
        String cmd  = request.getCommandName().toLowerCase();
        User   user = request.getUser();

        // --- Без авторизации ---
        if (cmd.equals("register")) {
            AuthService.AuthResult r = authService.register(user);
            return r.isOk() ? CommandResponse.ok(r.getMessage())
                            : CommandResponse.error(r.getMessage());
        }
        if (cmd.equals("login")) {
            AuthService.AuthResult r = authService.authenticate(user);
            if (!r.isOk()) return CommandResponse.error(r.getMessage());
            String role = authService.getRole(user.getLogin());
            return CommandResponse.ok("Login successful. Welcome, "
                    + user.getLogin() + "! Role: " + role);
        }

        // --- Все остальные требуют авторизации ---
        AuthService.AuthResult auth = authService.authenticate(user);
        if (!auth.isOk()) {
            return CommandResponse.error("Authorization required: " + auth.getMessage());
        }

        String login = user.getLogin();
        String role  = authService.getRole(login);

        // --- Только для ADMIN ---
        if (cmd.equals("promote_user") || cmd.equals("demote_admin")) {
            if (!"ADMIN".equals(role)) {
                return CommandResponse.error(
                        "Access denied: command '" + cmd + "' requires ADMIN role");
            }
        }

        ServerCommandHandler handler = handlers.get(cmd);
        if (handler == null) {
            return CommandResponse.error("Unknown command: " + cmd
                    + ". Type 'help' for available commands.");
        }

        try {
            return handler.handle(request, login, role);
        } catch (IllegalArgumentException e) {
            return CommandResponse.error("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return CommandResponse.error("Command error: " + e.getMessage());
        }
    }
}
