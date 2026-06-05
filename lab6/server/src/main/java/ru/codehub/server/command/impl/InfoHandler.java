package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code info}.
 * Возвращает метаданные коллекции: тип, размер, дату инициализации.
 */
public class InfoHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции.
     */
    public InfoHandler(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() { return "info"; }

    /**
     * Формирует строку с информацией о коллекции.
     *
     * @param request запрос (не используется).
     * @return ответ с типом, размером и датой инициализации.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        String info = "Type: " + manager.getCollectionType() + "\n"
                + "Size: " + manager.size() + "\n"
                + "Initialized: " + manager.getInitializationDate();
        return CommandResponse.ok(info);
    }
}
