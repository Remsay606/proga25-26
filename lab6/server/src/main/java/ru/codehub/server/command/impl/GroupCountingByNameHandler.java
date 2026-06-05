package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Обработчик команды {@code group_counting_by_name}.
 * Группирует элементы по названию и возвращает количество в каждой группе.
 * Использует Stream API ({@code Collectors.groupingBy}).
 */
public class GroupCountingByNameHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public GroupCountingByNameHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "group_counting_by_name"; }

    /**
     * Выполняет группировку через {@code Stream.collect(Collectors.groupingBy(...))}.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ со строкой вида "Name: N".
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        Map<String, Long> groups = manager.groupByName();
        if (groups.isEmpty()) return CommandResponse.ok("Collection is empty");
        String result = groups.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
        return CommandResponse.ok(result);
    }
}
