package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code remove_by_id}.
 * Удаляет элемент коллекции по переданному ID.
 */
public class RemoveByIdHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции.
     */
    public RemoveByIdHandler(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() { return "remove_by_id"; }

    /**
     * Парсит ID из аргументов запроса и удаляет соответствующий элемент.
     *
     * @param request запрос с аргументом args[0] = ID.
     * @return ответ об успешном удалении или об ошибке.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        if (request.getArgs().length == 0) return CommandResponse.error("ID required");
        try {
            long id = Long.parseLong(request.getArgs()[0]);
            boolean removed = manager.remove(id);
            return removed
                    ? CommandResponse.ok("Removed element with id=" + id)
                    : CommandResponse.error("No element with id=" + id);
        } catch (NumberFormatException e) {
            return CommandResponse.error("Invalid ID format: " + request.getArgs()[0]);
        }
    }
}
