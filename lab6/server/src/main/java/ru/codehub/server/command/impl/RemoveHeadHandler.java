package ru.codehub.server.command.impl;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;

import java.util.Optional;

/**
 * Обработчик команды {@code remove_head}.
 * Извлекает и удаляет первый элемент очереди (голову).
 */
public class RemoveHeadHandler implements ServerCommandHandler {

    private final CollectionManager manager;

    /** @param manager менеджер коллекции. */
    public RemoveHeadHandler(CollectionManager manager) { this.manager = manager; }

    @Override
    public String getCommandName() { return "remove_head"; }

    /**
     * Извлекает и удаляет голову очереди.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ с удалённым элементом или сообщение о пустой коллекции.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        Optional<MusicBand> head = manager.pollHead();
        return head.isPresent()
                ? CommandResponse.ok("Removed head: " + head.get())
                : CommandResponse.ok("Collection is empty");
    }
}
