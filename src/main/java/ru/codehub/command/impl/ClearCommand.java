package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;

/**
 * Команда для полной очистки коллекции.
 * Удаляет все элементы, находящиеся в текущий момент в памяти приложения.
 */
public class ClearCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с коллекцией музыкальных групп.
     */
    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "clear collection";
    }

    /**
     * Выполняет операцию очистки через CollectionManager и выводит подтверждение в консоль.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        collectionManager.clear();
        System.out.println("Collection cleared.");
    }
}