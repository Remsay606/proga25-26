package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.List;
import java.util.stream.Collectors;

/** Команда {@code filter_contains_name}: фильтр по подстроке имени. */
public class FilterContainsNameHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public FilterContainsNameHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "filter_contains_name"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        String[] args = request.getArgs();
        if (args.length == 0) return CommandResponse.error("Usage: filter_contains_name <substring>");
        List<MusicBand> filtered = manager.filterByNameContains(args[0]);
        if (filtered.isEmpty()) return CommandResponse.ok("No elements contain: " + args[0]);
        String body = filtered.stream()
                .map(MusicBand::toString)
                .collect(Collectors.joining("\n"));
        return CommandResponse.ok(body);
    }
}
