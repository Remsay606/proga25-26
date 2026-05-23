package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.io.CollectionWriter;

/**
 * Команда для сохранения текущего состояния коллекции в файл.
 * Использует реализацию {@link CollectionWriter} для записи данных по указанному пути.
 */
public class SaveCommand implements Command {
    /** Менеджер коллекции, предоставляющий данные для сохранения */
    private final CollectionManager collectionManager;

    /** Объект, отвечающий за логику записи данных в файл */
    private final CollectionWriter writer;

    /** Путь к файлу, в который будет произведено сохранение */
    private final String filePath;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер коллекции.
     * @param writer компонент для записи коллекции.
     * @param filePath путь к целевому файлу сохранения.
     */
    public SaveCommand(CollectionManager collectionManager, CollectionWriter writer, String filePath) {
        this.collectionManager = collectionManager;
        this.writer = writer;
        this.filePath = filePath;
    }

    /**
     * Возвращает уникальное имя команды.
     * @return строковое имя команды "save".
     */
    @Override
    public String getName() {
        return "save";
    }

    /**
     * Возвращает краткое описание назначения команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "save the collection to a file";
    }

    /**
     * Выполняет операцию записи коллекции в файл.
     * Извлекает данные из менеджера коллекции и передает их в writer.
     * В случае успеха или ошибки выводит соответствующее сообщение в консоль.
     * @param args аргументы команды (не требуются, так как путь задан при создании).
     */
    @Override
    public void execute(String[] args) {
        try {
            writer.write(collectionManager.getCollection(), filePath);
            System.out.println("Collection saved to " + filePath);
        } catch (Exception e) {
            System.out.println("Error saving collection: " + e.getMessage());
        }
    }
}