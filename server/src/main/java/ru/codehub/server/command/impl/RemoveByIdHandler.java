package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code remove_by_id id}: удаляет объект.
 * USER — только свои. ADMIN — любые.
 */
public class RemoveByIdHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public RemoveByIdHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "remove_by_id"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        String[] args = request.getArgs();
        if (args.length == 0) return CommandResponse.error("Usage: remove_by_id <id>");
        long id;
        try { id = Long.parseLong(args[0]); }
        catch (NumberFormatException e) { return CommandResponse.error("Invalid id: " + args[0]); }

        boolean isAdmin = "ADMIN".equals(role);
        CollectionManager.ModifyResult r = manager.remove(id, ownerLogin, isAdmin);
        return r.success ? CommandResponse.ok(r.message) : CommandResponse.error(r.message);
    }
}
