package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code clear}.
 * Полностью очищает коллекцию на сервере.
 */
public class ClearHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public ClearHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "clear"; }

    /**
     * Очищает коллекцию.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ об успешной очистке.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        manager.clear();
        return CommandResponse.ok("Collection cleared");
    }
}
