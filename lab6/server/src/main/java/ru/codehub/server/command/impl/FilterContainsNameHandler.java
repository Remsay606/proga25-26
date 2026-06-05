package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик команды {@code filter_contains_name}.
 * Возвращает элементы, чьё название содержит переданную подстроку.
 * Использует Stream API ({@code Stream.filter}).
 */
public class FilterContainsNameHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public FilterContainsNameHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "filter_contains_name"; }

    /**
     * Фильтрует коллекцию по подстроке в имени.
     *
     * @param request запрос с args[0] = подстрока для поиска.
     * @return ответ со списком совпадений.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        if (request.getArgs().length == 0) return CommandResponse.error("Substring required");
        String sub = request.getArgs()[0];
        List<MusicBand> result = manager.filterByNameContains(sub);
        if (result.isEmpty()) return CommandResponse.ok("No elements found containing: " + sub);
        String out = result.stream().map(MusicBand::toString).collect(Collectors.joining("\n"));
        return CommandResponse.ok(out);
    }
}
