package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.model.MusicBand;

import java.util.List;

/**
 * Команда для вывода всех элементов коллекции в стандартный поток вывода.
 * Используется для визуального контроля текущего содержимого коллекции музыкальных групп.
 */
public class ShowCommand implements Command {
    /** Менеджер коллекции для получения списка всех объектов. */
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     */
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Возвращает уникальное имя команды.
     * @return строковое имя команды "show".
     */
    @Override
    public String getName() {
        return "show";
    }

    /**
     * Возвращает краткое описание назначения команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "print all elements of the collection in string representation to the standard output stream";
    }

    /**
     * Выполняет вывод всех элементов коллекции.
     * Если коллекция пуста, выводит соответствующее сообщение. В противном случае
     * итерируется по списку элементов и выводит каждый из них в консоль.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        List<MusicBand> bands = collectionManager.getAll();
        if (bands.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }

        System.out.println("Collection elements:");
        for (MusicBand band : bands) {
            System.out.println(band);
        }
    }
}