package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code clear}: USER удаляет только свои объекты.
 * ADMIN удаляет все объекты коллекции.
 */
public class ClearHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public ClearHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "clear"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        boolean isAdmin = "ADMIN".equals(role);
        int removed;
        if (isAdmin) {
            removed = manager.clearAll();
            return CommandResponse.ok("[ADMIN] Removed all " + removed + " objects from collection");
        } else {
            removed = manager.clearOwned(ownerLogin);
            return CommandResponse.ok("Removed " + removed + " of your objects");
        }
    }
}
