package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Команда {@code remove_head}: удаляет первый элемент.
 * USER — только если он его владелец. ADMIN — всегда.
 */
public class RemoveHeadHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public RemoveHeadHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "remove_head"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        boolean isAdmin = "ADMIN".equals(role);
        CollectionManager.ModifyResult r = manager.removeHead(ownerLogin, isAdmin);
        if (!r.success) return CommandResponse.error(r.message);
        MusicBand b = r.band;
        return CommandResponse.ok("Removed head: " + (b != null ? b.toString() : ""));
    }
}
