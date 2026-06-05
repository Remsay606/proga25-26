package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Optional;

/**
 * Обработчик команды {@code add_if_max}.
 * Добавляет объект в коллекцию только если он больше текущего максимума.
 */
public class AddIfMaxHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public AddIfMaxHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "add_if_max"; }

    /**
     * Сравнивает переданный объект с максимумом коллекции.
     * Добавляет только если переданный объект больше.
     *
     * @param request запрос с объектом MusicBand.
     * @return ответ о результате операции.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        MusicBand band = request.getBand();
        if (band == null) return CommandResponse.error("No MusicBand provided");

        Optional<MusicBand> max = manager.getMax();
        if (max.isEmpty() || band.compareTo(max.get()) > 0) {
            MusicBand added = manager.add(band);
            return CommandResponse.ok("Added (is max): " + added.getName() + " id=" + added.getId());
        }
        return CommandResponse.ok("Not added: element is not greater than current max");
    }
}
