package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик команды {@code show}.
 * <p>
 * Возвращает все элементы коллекции, отсортированные по размеру группы
 * (количеству участников) с использованием Stream API и лямбда-выражений.
 * </p>
 */
public class ShowHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /**
     * @param manager менеджер коллекции.
     */
    public ShowHandler(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() { return "show"; }

    /**
     * Формирует строковое представление всей коллекции.
     * Элементы отсортированы по количеству участников (по возрастанию) — «по размеру».
     *
     * @param request запрос (аргументы не используются).
     * @return ответ со списком групп или сообщением о пустой коллекции.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        List<MusicBand> bands = manager.getAll().stream()
                .sorted(Comparator.comparing(MusicBand::getNumberOfParticipants))
                .collect(Collectors.toList());

        if (bands.isEmpty()) return CommandResponse.ok("Collection is empty");

        String result = bands.stream()
                .map(MusicBand::toString)
                .collect(Collectors.joining("\n"));
        return CommandResponse.ok(result);
    }
}
