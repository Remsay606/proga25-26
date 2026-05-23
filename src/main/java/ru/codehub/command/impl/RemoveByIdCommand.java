package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;

/**
 * Команда для удаления элемента из коллекции по его уникальному идентификатору (ID).
 * Используется для точечного управления данными в коллекции.
 */
public class RemoveByIdCommand implements Command {
    /** Менеджер коллекции, отвечающий за непосредственное удаление объекта. */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает уникальное имя команды.
     * @return строковое имя команды "remove_by_id".
     */
    @Override
    public String getName() {
        return "remove_by_id";
    }

    /**
     * Возвращает краткое описание назначения команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "delete an item from the collection by its id";
    }

    /**
     * Выполняет операцию удаления элемента.
     * Проверяет наличие обязательного аргумента (ID), пытается преобразовать его в число
     * и инициирует удаление через менеджер коллекции.
     * @param args массив аргументов, где первый элемент должен быть числовым ID.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: remove_by_id <id>");
            return;
        }

        try {
            long id = Long.parseLong(args[0]);
            if (collectionManager.remove(id)) {
                System.out.println("Music band removed successfully.");
            } else {
                System.out.println("No music band found with id: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid id format. Please provide a valid number.");
        }
    }
}