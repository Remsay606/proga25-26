package ru.codehub.common.network;

import ru.codehub.common.model.MusicBand;
import ru.codehub.common.model.User;

import java.io.Serializable;

/**
 * Объект-запрос, отправляемый клиентом на сервер по UDP.
 * <p>
 * Инкапсулирует команду, её аргументы и учётные данные пользователя
 * (паттерн <b>Command</b>). Объект сериализуется и помещается в тело UDP-датаграммы.
 * </p>
 *
 * <p><b>Лаб. 7:</b> к каждому запросу прикрепляется {@link User} (логин и пароль) —
 * согласно требованию «для идентификации пользователя отправлять логин и пароль
 * с каждым запросом». Сервер проверяет учётные данные перед выполнением команды.</p>
 *
 * <p>Команды и их аргументы — это объекты, а не «простые» строки: для add/update
 * внутри запроса лежит сериализованный {@link MusicBand}.</p>
 */
public class CommandRequest implements Serializable {

    private static final long serialVersionUID = 3L;

    /** Название команды (например, "add", "show", "remove_by_id"). */
    private final String commandName;

    /** Строковые аргументы команды (например, ID или подстрока для фильтра). */
    private final String[] args;

    /** Объект MusicBand для команд add/update (может быть null). */
    private final MusicBand band;

    /** Учётные данные пользователя, отправляются с каждым запросом. */
    private User user;

    /**
     * Создаёт запрос без объекта (для show, clear, info и т.п.).
     *
     * @param commandName название команды.
     * @param args        строковые аргументы (можно пустой массив).
     */
    public CommandRequest(String commandName, String... args) {
        this.commandName = commandName;
        this.args = args;
        this.band = null;
    }

    /**
     * Создаёт запрос с объектом MusicBand (для add, update, add_if_min).
     *
     * @param commandName название команды.
     * @param band        объект музыкальной группы.
     * @param args        строковые аргументы.
     */
    public CommandRequest(String commandName, MusicBand band, String... args) {
        this.commandName = commandName;
        this.args = args;
        this.band = band;
    }

    /** @return название команды. */
    public String getCommandName() { return commandName; }

    /** @return массив строковых аргументов (никогда не null). */
    public String[] getArgs() { return args != null ? args : new String[0]; }

    /** @return объект MusicBand или null. */
    public MusicBand getBand() { return band; }

    /** @return учётные данные пользователя (могут быть null до установки). */
    public User getUser() { return user; }

    /**
     * Прикрепляет учётные данные к запросу.
     *
     * @param user логин/пароль текущего пользователя.
     */
    public void setUser(User user) { this.user = user; }
}
