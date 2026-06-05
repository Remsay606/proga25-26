package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Optional;

/**
 * Обработчик команды {@code update}.
 * Обновляет элемент коллекции по ID, заменяя его данными из запроса.
 */
public class UpdateHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public UpdateHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "update"; }

    /**
     * Парсит ID из аргументов, получает объект из запроса и выполняет обновление.
     *
     * @param request запрос с args[0]=ID и объектом MusicBand.
     * @return ответ об успехе или ошибке.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        if (request.getArgs().length == 0) return CommandResponse.error("ID required");
        if (request.getBand() == null) return CommandResponse.error("No MusicBand provided");
        try {
            long id = Long.parseLong(request.getArgs()[0]);
            Optional<MusicBand> updated = manager.update(id, request.getBand());
            return updated.isPresent()
                    ? CommandResponse.ok("Updated: " + updated.get().getName())
                    : CommandResponse.error("No element with id=" + id);
        } catch (NumberFormatException e) {
            return CommandResponse.error("Invalid ID: " + request.getArgs()[0]);
        }
    }
}
