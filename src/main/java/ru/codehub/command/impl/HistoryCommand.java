package ru.codehub.command.impl;

import ru.codehub.command.Command;
import ru.codehub.command.CommandRegistry;

import java.util.List;

/**
 * Команда для вывода истории последних выполненных команд.
 * Показывает список из последних 7 (или менее) команд, которые были успешно вызваны пользователем.
 */
public class HistoryCommand implements Command {
    /** Реестр команд, из которого извлекается накопленная история */
    private final CommandRegistry registry;

    /**
     * Конструктор команды.
     * @param registry реестр команд, обеспечивающий доступ к списку истории.
     */
    public HistoryCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "history".
     */
    @Override
    public String getName() {
        return "history";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "display the last 7 commands (without their arguments)";
    }

    /**
     * Выполняет вывод списка последних команд.
     * Если история пуста, выводит соответствующее уведомление.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        List<String> history = registry.getHistory();
        if (history.isEmpty()) {
            System.out.println("No command history.");
            return;
        }

        System.out.println("Last " + history.size() + " commands:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + history.get(i));
        }
    }
}