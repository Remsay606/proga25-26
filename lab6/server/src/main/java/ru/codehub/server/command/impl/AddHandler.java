package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code add}.
 * Добавляет переданный объект {@link MusicBand} в коллекцию.
 */
public class AddHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции.
     */
    public AddHandler(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() { return "add"; }

    /**
     * Проверяет наличие объекта в запросе, добавляет его в коллекцию.
     *
     * @param request запрос с объектом MusicBand.
     * @return ответ с ID добавленного элемента или ошибкой.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        MusicBand band = request.getBand();
        if (band == null) return CommandResponse.error("No MusicBand object provided");
        MusicBand added = manager.add(band);
        return CommandResponse.ok("Added: " + added.getName() + " (id=" + added.getId() + ")");
    }
}
