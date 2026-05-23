package ru.codehub.util;

import ru.codehub.input.UserInputReader;
import ru.codehub.model.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Фабрика для создания экземпляров MusicBand.
 * Обеспечивает логику чтения полей с консоли и обработку ошибок ввода.
 */
public class MusicBandFactory {
    private final IdGenerator idGenerator;

    public MusicBandFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * Создает новую группу с автоматической генерацией ID.
     * @param inputReader источник ввода.
     * @return полностью инициализированный объект MusicBand.
     */
    public MusicBand create(UserInputReader inputReader) {
        MusicBand band = readMusicBand(inputReader);
        band.setId(idGenerator.nextId());
        return band;
    }

    /**
     * Создает объект без установки ID (например, для последующего обновления существующего элемента).
     */
    public MusicBand createWithoutId(UserInputReader inputReader) {
        return readMusicBand(inputReader);
    }

    /**
     * Инкапсулирует процесс последовательного чтения всех атрибутов музыкальной группы.
     */
    private MusicBand readMusicBand(UserInputReader reader) {
        String name = readRequiredString(reader, "Enter name: ", "name");
        Long x = readRequiredLong(reader, "Enter x coordinate (max 52): ", "x coordinate", null, 52L);
        Double y = readRequiredDouble(reader, "Enter y coordinate: ", "y coordinate");
        Long numberOfParticipants = readRequiredLong(reader, "Enter number of participants: ", "number of participants", 1L, null);
        MusicGenre genre = readGenre(reader);
        Studio studio = readStudio(reader);

        Coordinates coordinates = new Coordinates(x, y);
        return new MusicBand(name, coordinates, numberOfParticipants, genre, studio);
    }

    /**
     * Считывает строку, проверяя, что она не является пустой.
     */
    private String readRequiredString(UserInputReader reader, String prompt, String fieldName) {
        while (true) {
            String input = reader.readLine(prompt).trim();
            if (!input.isEmpty()) {
                return input;
            }
            if (!reader.isInteractive()) {
                throw new IllegalArgumentException("Field '" + fieldName + "' cannot be empty");
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    /**
     * Считывает целое число с проверкой границ [min, max] и валидацией формата через NumbParser.
     */
    private Long readRequiredLong(UserInputReader reader, String prompt, String fieldName, Long min, Long max) {
        while (true) {
            String input = reader.readLine(prompt).trim();
            if (input.isEmpty()) {
                if (!reader.isInteractive()) {
                    throw new IllegalArgumentException("Field '" + fieldName + "' cannot be empty");
                }
                System.out.println("This field cannot be empty. Please try again.");
                continue;
            }
            try {
                long value = NumbParser.parseLong(input);
                if (min != null && value < min) {
                    if (!reader.isInteractive()) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' must be at least " + min);
                    }
                    System.out.println("Value must be at least " + min + ". Please try again.");
                    continue;
                }
                if (max != null && value > max) {
                    if (!reader.isInteractive()) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' must be at most " + max);
                    }
                    System.out.println("Value must be at most " + max + ". Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException | ArithmeticException e) {
                if (!reader.isInteractive()) {
                    throw new IllegalArgumentException("Invalid number format for field '" + fieldName + "': " + input);
                }
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    /**
     * Считывает число с плавающей точкой, используя NumbParser для обеспечения точности.
     */
    private Double readRequiredDouble(UserInputReader reader, String prompt, String fieldName) {
        while (true) {
            String input = reader.readLine(prompt);
            if (input == null || input.trim().isEmpty()) {
                if (!reader.isInteractive()) throw new IllegalArgumentException("Empty field");
                continue;
            }
            try {
                String cleaned = input.trim().replace(',', '.');
                // Обрезаем дробную часть до 15 значимых цифр
                int dot = cleaned.indexOf('.');
                if (dot != -1 && cleaned.length() - dot - 1 > 15) {
                    cleaned = cleaned.substring(0, dot + 16); // dot + 1 + 15 знаков
                }
                double value = new BigDecimal(cleaned).doubleValue();
                if (!reader.isInteractive()) {
                    System.out.println("DEBUG: Y принят как " + cleaned);
                }
                return value;
            } catch (Exception e) {
                if (!reader.isInteractive()) throw new IllegalArgumentException("Bad double: " + input);
                System.out.println("Ошибка формата.");
            }
        }
    }

    /**
     * Выводит список доступных жанров и считывает выбор пользователя.
     */
    private MusicGenre readGenre(UserInputReader reader) {
        MusicGenre[] genres = MusicGenre.values();
        String options = Arrays.stream(genres)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (reader.isInteractive()) {
            System.out.println("Available genres: " + options);
        }
        while (true) {
            String input = reader.readLine("Enter genre (name or number): ").trim();
            if (input.isEmpty()) {
                if (!reader.isInteractive()) throw new IllegalArgumentException("Field 'genre' cannot be empty");
                System.out.println("This field cannot be empty. Please try again.");
                continue;
            }
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < genres.length) {
                    return genres[index];
                }
            } catch (NumberFormatException e) {
                //
            }
            try {
                return MusicGenre.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (!reader.isInteractive()) throw new IllegalArgumentException("Invalid genre: " + input);
                System.out.println("Invalid genre. Please choose from: " + options);
            }
        }
    }

    /**
     * Считывает информацию о студии. Поля могут быть пустыми.
     */
    private Studio readStudio(UserInputReader reader) {
        String name = reader.readLine("Enter studio name (or empty for null): ").trim();
        String address = reader.readLine("Enter studio address (or empty for null): ").trim();

        name = name.isEmpty() ? null : name;
        address = address.isEmpty() ? null : address;

        return new Studio(name, address);
    }
}