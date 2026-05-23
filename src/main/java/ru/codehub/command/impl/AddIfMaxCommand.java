package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.input.UserInputReader;
import ru.codehub.model.MusicBand;
import ru.codehub.util.MusicBandFactory;

import java.util.Optional;

/**
 * Команда для добавления нового элемента в коллекцию, если он превышает максимальный элемент.
 * Сравнение происходит на основе логики, определенной в методе compareTo класса MusicBand.
 */
public class AddIfMaxCommand implements Command {
    private final CollectionManager collectionManager;
    private final UserInputReader inputReader;
    private final MusicBandFactory bandFactory;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для работы с коллекцией.
     * @param inputReader объект для чтения ввода пользователя.
     * @param bandFactory фабрика для создания объектов музыкальных групп.
     */
    public AddIfMaxCommand(CollectionManager collectionManager, UserInputReader inputReader, MusicBandFactory bandFactory) {
        this.collectionManager = collectionManager;
        this.inputReader = inputReader;
        this.bandFactory = bandFactory;
    }

    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "add a new element to the collection if its value exceeds the value of the largest element in the collection";
    }

    /**
     * Выполняет проверку и добавление элемента.
     * Считывает данные новой группы, находит текущий максимум в коллекции
     * и добавляет элемент только в случае, если он больше максимума или если коллекция пуста.
     * @param args аргументы команды (не требуются).
     */
    @Override
    public void execute(String[] args) {
        try {
            MusicBand band = bandFactory.create(inputReader);
            Optional<MusicBand> max = collectionManager.getMax();

            if (max.isEmpty() || band.compareTo(max.get()) > 0) {
                MusicBand added = collectionManager.add(band);
                System.out.println("Music band added successfully with id: " + added.getId());
            } else {
                System.out.println("Element was not added because it is not greater than the maximum.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}