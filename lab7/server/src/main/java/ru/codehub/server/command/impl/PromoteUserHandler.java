package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.auth.AuthService;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code promote_user <login>}: повышает пользователя до ADMIN.
 * Доступна только для ADMIN (проверяется в диспетчере).
 */
public class PromoteUserHandler implements ServerCommandHandler {

    private final AuthService authService;

    public PromoteUserHandler(AuthService authService) { this.authService = authService; }

    @Override public String getCommandName() { return "promote_user"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) {
            return CommandResponse.error("Usage: promote_user <login>");
        }
        String targetLogin = args[0].trim();
        if (targetLogin.equals(ownerLogin)) {
            return CommandResponse.error("You are already an admin");
        }
        AuthService.AuthResult r = authService.promoteToAdmin(targetLogin);
        return r.isOk() ? CommandResponse.ok(r.getMessage())
                        : CommandResponse.error(r.getMessage());
    }
}
