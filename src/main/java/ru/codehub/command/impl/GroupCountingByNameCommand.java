package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;

import java.util.Map;

/**
 * Команда для группировки элементов коллекции по их имени.
 * Выводит количество элементов для каждой уникальной группы имен.
 */
public class GroupCountingByNameCommand implements Command {
    /** Менеджер коллекции для выполнения операций группировки */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public GroupCountingByNameCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "group_counting_by_name".
     */
    @Override
    public String getName() {
        return "group_counting_by_name";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "group the collection elements by the name field value and display the number of elements in each group";
    }

    /**
     * Выполняет группировку элементов через CollectionManager и выводит результат в консоль.
     * Если коллекция пуста, выводит соответствующее сообщение.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        Map<String, Long> groups = collectionManager.groupByName();
        if (groups.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        System.out.println("Groups by name:");
        for (Map.Entry<String, Long> entry : groups.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}