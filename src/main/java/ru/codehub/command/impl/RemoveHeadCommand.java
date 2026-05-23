package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.model.MusicBand;

import java.util.Optional;

/**
 * Команда для удаления и вывода первого элемента коллекции.
 * Позволяет извлечь объект, который находится в начале списка/очереди.
 */
public class RemoveHeadCommand implements Command {
    /** Менеджер коллекции для выполнения операции извлечения первого элемента. */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public RemoveHeadCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает уникальное имя команды.
     * @return строковое имя команды "remove_head".
     */
    @Override
    public String getName() {
        return "remove_head";
    }

    /**
     * Возвращает краткое описание назначения команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "bring out the first element of the collection and delete it";
    }

    /**
     * Выполняет операцию извлечения "головы" коллекции через CollectionManager.
     * Если коллекция не пуста, выводит строковое представление удаленного элемента,
     * в противном случае сообщает, что коллекция пуста.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        Optional<MusicBand> head = collectionManager.pollHead();
        if (head.isPresent()) {
            System.out.println("Removed element:");
            System.out.println(head.get());
        } else {
            System.out.println("Collection is empty.");
        }
    }
}