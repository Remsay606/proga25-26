package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.command.ServerCommandHandler;

/**
 * Обработчик команды {@code help}.
 * Возвращает список всех доступных команд и их описание.
 */
public class HelpHandler implements ServerCommandHandler {

    private static final String HELP_TEXT =
            "Available commands:\n" +
            "  help                         - show this help\n" +
            "  info                         - show collection info\n" +
            "  show                         - show all elements (sorted by size)\n" +
            "  add                          - add new element (interactive)\n" +
            "  update <id>                  - update element by id (interactive)\n" +
            "  remove_by_id <id>            - remove element by id\n" +
            "  clear                        - clear the collection\n" +
            "  remove_head                  - remove and show head element\n" +
            "  add_if_max                   - add if greater than max (interactive)\n" +
            "  min_by_creation_date         - show element with min creation date\n" +
            "  group_counting_by_name       - group elements by name\n" +
            "  filter_contains_name <sub>   - filter by name substring\n" +
            "  exit                         - exit client";

    @Override
    public String getCommandName() { return "help"; }

    /**
     * Возвращает справочный текст со всеми командами.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ со справкой.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        return CommandResponse.ok(HELP_TEXT);
    }
}
