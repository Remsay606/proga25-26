package ru.codehub.command.impl;

import ru.codehub.command.Command;

/**
 * Команда для завершения работы приложения.
 * Данная команда сигнализирует системе о необходимости выхода без автоматического сохранения данных.
 */
public class ExitCommand implements Command {

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "exit".
     */
    @Override
    public String getName() {
        return "exit";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "exit the program (without saving to a file)";
    }

    /**
     * Выполняет вывод информационного сообщения о завершении работы.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        System.out.println("Exiting program...");
    }

    /**
     * Указывает системе, что данная команда является терминирующей.
     * @return всегда true, что служит сигналом для остановки цикла обработки команд.
     */
    @Override
    public boolean isTerminating() {
        return true;
    }
}