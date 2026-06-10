package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Optional;

/** Команда {@code min_by_creation_date}: старейший элемент. */
public class MinByCreationDateHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public MinByCreationDateHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "min_by_creation_date"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        Optional<MusicBand> min = manager.getMinByCreationDate();
        return min.map(b -> CommandResponse.ok(b.toString()))
                .orElse(CommandResponse.ok("Collection is empty"));
    }
}
