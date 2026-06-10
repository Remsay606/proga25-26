package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/** Команда {@code info}: сведения о коллекции. */
public class InfoHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    public InfoHandler(CollectionManager manager) { this.manager = manager; }

    @Override public String getCommandName() { return "info"; }

    @Override
    public CommandResponse handle(CommandRequest request, String ownerLogin, String role) {
        String info = "Collection type: " + manager.getCollectionType() + "\n"
                + "Initialization date: " + manager.getInitializationDate() + "\n"
                + "Number of elements: " + manager.size();
        return CommandResponse.ok(info);
    }
}
