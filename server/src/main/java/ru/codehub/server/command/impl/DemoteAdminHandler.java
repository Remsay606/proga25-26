package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.auth.AuthService;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code demote_admin <login>}: понижает ADMIN до USER.
 * Нельзя понизить самого себя.
 * Доступна только для ADMIN (проверяется в диспетчере).
 */
public class DemoteAdminHandler implements ServerCommandHandler {

    private final AuthService authService;

    public DemoteAdminHandler(AuthService authService) { this.authService = authService; }

    @Override public String getCommandName() { return "demote_admin"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) {
            return CommandResponse.error("Usage: demote_admin <login>");
        }
        String targetLogin = args[0].trim();
        AuthService.AuthResult r = authService.demoteToUser(targetLogin, ownerLogin);
        return r.isOk() ? CommandResponse.ok(r.getMessage())
                        : CommandResponse.error(r.getMessage());
    }
}
