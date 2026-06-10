package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Map;
import java.util.stream.Collectors;

/** Команда {@code group_counting_by_name}: группировка по имени. */
public class GroupCountingByNameHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public GroupCountingByNameHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "group_counting_by_name"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        Map<String, Long> groups = manager.groupByName();
        if (groups.isEmpty()) return CommandResponse.ok("Collection is empty");
        String body = groups.entrySet().stream()
                .map(e -> "  " + e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
        return CommandResponse.ok("Groups by name:\n" + body);
    }
}
