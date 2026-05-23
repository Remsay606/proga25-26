package ru.codehub.command.impl;

import ru.codehub.command.Command;
import ru.codehub.command.CommandRegistry;

/**
 * Команда для вывода справочной информации по всем доступным командам приложения.
 */
public class HelpCommand implements Command {
    /** Реестр, содержащий список всех зарегистрированных команд */
    private final CommandRegistry registry;

    /**
     * Конструктор команды.
     * @param registry реестр команд, из которого будет извлекаться информация для справки.
     */
    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "help".
     */
    @Override
    public String getName() {
        return "help";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "output help for available commands";
    }

    /**
     * Выполняет вывод списка всех команд, зарегистрированных в системе.
     * Для каждой команды выводится её имя и краткое описание в форматированном виде.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        System.out.println("Available commands:");
        for (Command command : registry.getAllCommands()) {
            System.out.printf("  %-30s : %s%n", command.getName(), command.getDescription());
        }
    }
}