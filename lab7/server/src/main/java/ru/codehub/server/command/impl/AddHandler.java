package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/** Команда {@code add}: добавляет объект; владелец — текущий пользователь. */
public class AddHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public AddHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "add"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role)
            throws Exception {
        MusicBand band = request.getBand();
        if (band == null) return CommandResponse.error("No MusicBand object provided");
        MusicBand added = manager.add(band, ownerLogin);
        return CommandResponse.ok("Added: " + added.getName() + " (id=" + added.getId() + ")");
    }
}
