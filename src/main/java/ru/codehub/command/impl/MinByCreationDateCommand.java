package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.model.MusicBand;

import java.util.Optional;

/**
 * Команда для поиска и вывода элемента коллекции с минимальной датой создания.
 * Позволяет найти самую "старую" запись в текущей коллекции музыкальных групп.
 */
public class MinByCreationDateCommand implements Command {
    /** Менеджер коллекции для выполнения операций поиска */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public MinByCreationDateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает имя команды.
     * @return строковое имя команды "min_by_creation_date".
     */
    @Override
    public String getName() {
        return "min_by_creation_date";
    }

    /**
     * Возвращает описание команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "retrieve any object from the collection whose creationDate field value is the minimum";
    }

    /**
     * Выполняет поиск элемента с минимальной датой создания через CollectionManager.
     * Если коллекция не пуста, выводит найденный объект, иначе сообщает о пустой коллекции.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        Optional<MusicBand> min = collectionManager.getMinByCreationDate();
        if (min.isPresent()) {
            System.out.println("Element with minimum creation date:");
            System.out.println(min.get());
        } else {
            System.out.println("Collection is empty.");
        }
    }
}