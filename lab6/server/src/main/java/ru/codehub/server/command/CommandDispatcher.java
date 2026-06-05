package ru.codehub.server.command;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Диспетчер серверных команд.
 * <p>
 * Реализует паттерн <b>Facade</b>: скрывает сложность маршрутизации
 * запросов к конкретным обработчикам {@link ServerCommandHandler}.
 * </p>
 * <p>
 * Все обработчики регистрируются при запуске сервера через {@link #register}.
 * При получении запроса от клиента сервер вызывает {@link #dispatch}.
 * </p>
 */
public class CommandDispatcher {

    /** Реестр обработчиков: имя команды → обработчик. */
    private final Map<String, ServerCommandHandler> handlers = new HashMap<>();

    /**
     * Регистрирует обработчик команды.
     *
     * @param handler реализация {@link ServerCommandHandler}.
     */
    public void register(ServerCommandHandler handler) {
        handlers.put(handler.getCommandName(), handler);
    }

    /**
     * Маршрутизирует входящий запрос к соответствующему обработчику.
     * Если обработчик не найден — возвращает ответ с ошибкой.
     *
     * @param request запрос от клиента.
     * @return ответ сервера.
     */
    public CommandResponse dispatch(CommandRequest request) {
        ServerCommandHandler handler = handlers.get(request.getCommandName());
        if (handler == null) {
            return CommandResponse.error("Unknown command: " + request.getCommandName());
        }
        try {
            return handler.handle(request);
        } catch (Exception e) {
            return CommandResponse.error("Command execution error: " + e.getMessage());
        }
    }
}
