package ru.codehub.server.command;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;

/**
 * Интерфейс обработчика серверной команды.
 * <p>
 * Реализует паттерн <b>Command</b>: каждый обработчик инкапсулирует
 * одну операцию над коллекцией и возвращает результат в виде {@link CommandResponse}.
 * </p>
 * <p>
 * Паттерн <b>Strategy</b>: набор реализаций хранится в {@link CommandDispatcher},
 * конкретная реализация выбирается по имени команды из {@link CommandRequest}.
 * </p>
 */
public interface ServerCommandHandler {

    /**
     * Возвращает имя команды, которую обрабатывает данный хендлер.
     * Используется {@link CommandDispatcher} для маршрутизации запросов.
     *
     * @return имя команды (строчные буквы, например "add", "show").
     */
    String getCommandName();

    /**
     * Выполняет команду и возвращает результат.
     *
     * @param request запрос, содержащий аргументы и возможный объект {@link ru.codehub.common.model.MusicBand}.
     * @return ответ с результатом выполнения.
     */
    CommandResponse handle(CommandRequest request);
}
