package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.model.MusicBand;

import java.util.List;

/**
 * Команда для фильтрации и вывода элементов коллекции, имя которых содержит заданную подстроку.
 * Позволяет осуществлять поиск по части названия музыкальной группы.
 */
public class FilterContainsNameCommand implements Command {
    /** Менеджер коллекции для выполнения операций поиска */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public FilterContainsNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "filter_contains_name".
     */
    @Override
    public String getName() {
        return "filter_contains_name";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "display elements whose name field contains the specified substring";
    }

    /**
     * Выполняет поиск элементов в коллекции по заданной подстроке.
     * Объединяет все переданные аргументы в одну строку поиска и выводит подходящие объекты.
     * @param args массив аргументов, где элементы составляют искомую подстроку.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: filter_contains_name <substring>");
            return;
        }

        // Объединение аргументов в случае, если подстрока содержит пробелы
        String substring = String.join(" ", args);
        List<MusicBand> filtered = collectionManager.filterByNameContains(substring);

        if (filtered.isEmpty()) {
            System.out.println("No elements found containing: " + substring);
            return;
        }

        System.out.println("Elements containing '" + substring + "':");
        for (MusicBand band : filtered) {
            System.out.println(band);
        }
    }
}