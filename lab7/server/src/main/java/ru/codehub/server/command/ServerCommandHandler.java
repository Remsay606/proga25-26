package ru.codehub.server.command;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;

/**
 * Интерфейс обработчика серверной команды (паттерн Command).
 *
 * <p><b>Роли:</b> каждый хендлер получает роль аутентифицированного пользователя.
 * Хендлеры модификации используют её для bypass-проверки владельца у ADMIN.</p>
 */
public interface ServerCommandHandler {

    /** @return имя команды (например, "add", "show"). */
    String getCommandName();

    /**
     * Выполняет команду.
     *
     * @param request     запрос с аргументами и объектом.
     * @param ownerLogin  логин текущего пользователя.
     * @param role        роль текущего пользователя: "USER" или "ADMIN".
     * @return результат выполнения.
     * @throws Exception при ошибке БД или внутренней ошибке.
     */
    CommandResponse handle(CommandRequest request, String ownerLogin, String role) throws Exception;
}
