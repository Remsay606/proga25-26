package ru.codehub.common.network;

import ru.codehub.common.model.MusicBand;

import java.io.Serializable;

/**
 * Объект-запрос, отправляемый клиентом на сервер по UDP.
 * <p>
 * Инкапсулирует команду и её аргументы — паттерн <b>Command</b>.
 * Объект сериализуется и помещается в тело UDP-датаграммы.
 * </p>
 *
 * <pre>
 * Примеры:
 *   new CommandRequest("show")              — команда без аргументов
 *   new CommandRequest("remove_by_id", "5") — команда с числовым аргументом
 *   new CommandRequest("add", band)         — команда с объектом MusicBand
 * </pre>
 */
public class CommandRequest implements Serializable {

    /** Название команды (например, "add", "show", "remove_by_id"). */
    private final String commandName;

    /** Строковые аргументы команды (например, ID или подстрока для фильтра). */
    private final String[] args;

    /**
     * Объект MusicBand, передаваемый вместе с командой (например, для add/update).
     * Может быть null, если команда не требует объекта.
     */
    private final MusicBand band;

    /**
     * Создаёт запрос без объекта (для команд типа show, clear, info).
     *
     * @param commandName название команды.
     * @param args        строковые аргументы (можно передать пустой массив).
     */
    public CommandRequest(String commandName, String... args) {
        this.commandName = commandName;
        this.args = args;
        this.band = null;
    }

    /**
     * Создаёт запрос с объектом MusicBand (для команд add, update, add_if_max).
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

    /** @return массив строковых аргументов. */
    public String[] getArgs() { return args; }

    /** @return объект MusicBand или null. */
    public MusicBand getBand() { return band; }
}
