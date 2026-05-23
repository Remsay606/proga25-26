package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;

import java.text.SimpleDateFormat;

/**
 * Команда для вывода основной информации о коллекции.
 * Отображает тип коллекции, дату инициализации и текущее количество элементов.
 */
public class InfoCommand implements Command {
    /** Менеджер коллекции для получения метаданных */
    private final CollectionManager collectionManager;

    /** Форматтер для человекочитаемого вывода даты */
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "info".
     */
    @Override
    public String getName() {
        return "info";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "print information about the collection (type, initialization date, number of elements, etc.) to the standard output stream";
    }

    /**
     * Выполняет вывод информации о коллекции в консоль.
     * Выводит тип реализации коллекции, дату её создания/загрузки и размер.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        System.out.println("Collection information:");
        System.out.println("  Type: " + collectionManager.getCollectionType());
        System.out.println("  Initialization date: " + FORMATTER.format(collectionManager.getInitializationDate()));
        System.out.println("  Number of elements: " + collectionManager.size());
    }
}