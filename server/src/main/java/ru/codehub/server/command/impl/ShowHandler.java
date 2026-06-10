package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.List;
import java.util.stream.Collectors;

/** Команда {@code show}: все элементы отсортированные по размеру. */
public class ShowHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public ShowHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "show"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        List<MusicBand> all = manager.getAll();
        if (all.isEmpty()) return CommandResponse.ok("Collection is empty");
        String body = all.stream()
                .map(MusicBand::toString)
                .collect(Collectors.joining("\n"));
        return CommandResponse.ok(body);
    }
}
