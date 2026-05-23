package ru.codehub.command;

import java.util.*;

/**
 * Реестр команд приложения.
 * Хранит список доступных команд и историю последних выполненных операций.
 */
public class CommandRegistry {
    /** Словарь доступных команд, где ключ — имя команды */
    private final Map<String, Command> commands;

    /** Список для хранения истории имен вызванных команд */
    private final LinkedList<String> history;

    /** Максимальное количество записей в истории */
    private static final int MAX_HISTORY_SIZE = 7;

    public CommandRegistry() {
        this.commands = new LinkedHashMap<>();
        this.history = new LinkedList<>();
    }

    /**
     * Регистрирует новую команду в системе.
     * @param command объект команды для регистрации.
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * Поиск команды по её имени.
     * @param name название команды.
     * @return Optional, содержащий команду, если она найдена.
     */
    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    /**
     * Возвращает коллекцию всех зарегистрированных команд.
     */
    public Collection<Command> getAllCommands() {
        return commands.values();
    }

    /**
     * Добавляет имя выполненной команды в историю.
     * При превышении лимита (7 записей) самая старая запись удаляется.
     * @param commandName имя выполненной команды.
     */
    public void addToHistory(String commandName) {
        history.addLast(commandName);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    /**
     * Возвращает копию текущего списка истории команд.
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
}