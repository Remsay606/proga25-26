package ru.codehub.command.impl;

import ru.codehub.collection.CollectionManager;
import ru.codehub.command.Command;
import ru.codehub.input.UserInputReader;
import ru.codehub.model.MusicBand;
import ru.codehub.util.MusicBandFactory;
import ru.codehub.util.NumbParser;

import java.util.Arrays;
import java.util.Optional;

/**
 * Команда для обновления существующего элемента коллекции по его идентификатору (ID).
 * Пользователь вводит ID, и если элемент найден, система запрашивает новые данные для объекта.
 */
public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;
    private final UserInputReader inputReader;
    private final MusicBandFactory bandFactory;

    /**
     * Конструктор команды.
     * @param collectionManager менеджер для взаимодействия с данными коллекции.
     * @param inputReader объект для чтения обновленных данных от пользователя.
     * @param bandFactory фабрика для создания нового экземпляра музыкальной группы.
     */
    public UpdateCommand(CollectionManager collectionManager, UserInputReader inputReader, MusicBandFactory bandFactory) {
        this.collectionManager = collectionManager;
        this.inputReader = inputReader;
        this.bandFactory = bandFactory;
    }

    /**
     * Возвращает уникальное имя команды.
     * @return строковое имя команды "update".
     */
    @Override
    public String getName() {
        return "update";
    }

    /**
     * Возвращает краткое описание назначения команды.
     * @return текст описания на русском языке.
     */
    @Override
    public String getDescription() {
        return "update the value of a collection element whose id is equal to the specified value";
    }

    /**
     * Выполняет процедуру обновления элемента.
     * 1. Проверяет наличие ID в аргументах.
     * 2. Проверяет существование элемента с таким ID в коллекции.
     * 3. Если элемент найден, запрашивает ввод новых полей через фабрику (без смены ID).
     * 4. Заменяет старые данные новыми в менеджере коллекции.
     * @param args массив аргументов, где первый элемент — целевой ID.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: update <id> [field1 field2 ...]");
            return;
        }
        try {
            long id = Long.parseLong(args[0]);
            Optional<MusicBand> optionalBand = collectionManager.getById(id);

            if (optionalBand.isEmpty()) {
                System.out.println("No music band found with id: " + id);
                return;
            }
            MusicBand band = optionalBand.get();
            String[] fieldsToUpdate;
            if (args.length > 1) {
                fieldsToUpdate = Arrays.copyOfRange(args, 1, args.length);
            } else {
                System.out.println("Current element: " + band);
                System.out.println("Enter fields to change separated by space (name, x, y, participants, genre, studio_name, studio_address):");
                fieldsToUpdate = inputReader.readLine("> ").trim().toLowerCase().split("\\s+");
            }

            for (String field : fieldsToUpdate) {
                boolean fieldUpdated = false;
                while (!fieldUpdated) {
                    try {
                        switch (field.toLowerCase()) {
                            case "name":
                                band.setName(inputReader.readLine("Enter new name: "));
                                fieldUpdated = true;
                                break;
                            case "x":
                                band.getCoordinates().setX(NumbParser.parseLong(inputReader.readLine("Enter new x: ")));
                                fieldUpdated = true;
                                break;
                            case "y":
                                band.getCoordinates().setY(NumbParser.parseDouble(inputReader.readLine("Enter new y: ")));
                                fieldUpdated = true;
                                break;
                            case "participants":
                                band.setNumberOfParticipants(NumbParser.parseLong(inputReader.readLine("Enter new number of participants: ")));
                                fieldUpdated = true;
                                break;
                            case "genre":
                                System.out.println("Available genres: PSYCHEDELIC_ROCK, RAP, BLUES, POP, MATH_ROCK");
                                fieldUpdated = true;
                                break;
                            case "studio_name":
                                String sName = inputReader.readLine("Enter new studio name: ").trim();
                                band.getStudio().setName(sName.isEmpty() ? null : sName);
                                fieldUpdated = true;
                                break;
                            case "studio_address":
                                String sAddr = inputReader.readLine("Enter new studio address: ").trim();
                                band.getStudio().setAddress(sAddr.isEmpty() ? null : sAddr);
                                fieldUpdated = true;
                                break;
                            default:
                                System.out.println("Unknown field: " + field + ". Skipping...");
                                fieldUpdated = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input for " + field + ": " + e.getMessage() + ". Try again.");
                    }
                }
            }
            collectionManager.update(id, band);
            System.out.println("Music band updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }
}