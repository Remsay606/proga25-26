package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/** Команда {@code add_if_min}: добавляет если размер меньше минимального. */
public class AddIfMinHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public AddIfMinHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "add_if_min"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        MusicBand band = request.getBand();
        if (band == null) return CommandResponse.error("No MusicBand object provided");
        CollectionManager.ModifyResult r = manager.addIfMin(band, ownerLogin);
        return r.success ? CommandResponse.ok(r.message) : CommandResponse.error(r.message);
    }
}
