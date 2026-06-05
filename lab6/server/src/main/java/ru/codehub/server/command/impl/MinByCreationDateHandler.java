package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code min_by_creation_date}.
 * Возвращает элемент с наиболее ранней датой создания.
 */
public class MinByCreationDateHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public MinByCreationDateHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "min_by_creation_date"; }

    /**
     * Находит элемент с наименьшей датой создания через Stream API.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ с найденным элементом или сообщение о пустой коллекции.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        return manager.getMinByCreationDate()
                .map(b -> CommandResponse.ok("Min by creation date: " + b))
                .orElse(CommandResponse.ok("Collection is empty"));
    }
}
