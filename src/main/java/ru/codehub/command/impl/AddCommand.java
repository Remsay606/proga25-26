package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.input.UserInputReader;
import ru.codehub.model.MusicBand;
import ru.codehub.util.MusicBandFactory;

/**
 * Команда для добавления нового элемента в коллекцию.
 */
public class AddCommand implements Command {
    private final CollectionManager collectionManager;
    private final UserInputReader inputReader;
    private final MusicBandFactory bandFactory;

    public AddCommand(CollectionManager collectionManager, UserInputReader inputReader, MusicBandFactory bandFactory) {
        this.collectionManager = collectionManager;
        this.inputReader = inputReader;
        this.bandFactory = bandFactory;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add a new element to the collection";
    }

    /**
     * Выполняет чтение данных о новой группе и добавляет её в менеджере коллекции.
     * @param args аргументы команды (в данном случае не используются).
     */
    @Override
    public void execute(String[] args) {
        try {
            MusicBand band = bandFactory.create(inputReader);
            collectionManager.add(band);
            System.out.println("Music band added successfully.");
        } catch (Exception e) {
            System.out.println("Error adding music band: " + e.getMessage());
            if (!inputReader.isInteractive()) {
                System.out.println("Cleaning up script lines until next command...");
                while (inputReader.hasNextLine()) {
                    String line = inputReader.readLine(); // выкидываем строку
                    if (line == null || line.trim().isEmpty()) break;
                    // Остановимся, если строка похожа на команду (например, содержит 'add' или 'show')
                    if (line.matches("^[a-z_]+.*$") && !line.matches("^-?\\d+(\\.\\d+)?$")) break;
                }
            }
        }
    }
}