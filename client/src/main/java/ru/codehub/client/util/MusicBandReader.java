package ru.codehub.client.util;

import ru.codehub.common.model.*;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Утилитный класс для интерактивного ввода объекта {@link MusicBand} с консоли.
 * <p>
 * Реализует паттерн <b>Factory Method</b>: инкапсулирует создание сложного объекта
 * через последовательный опрос пользователя с валидацией каждого поля.
 * </p>
 */
public class MusicBandReader {

    private final Scanner scanner;

    /**
     * @param scanner источник пользовательского ввода.
     */
    public MusicBandReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Читает все поля {@link MusicBand} из консоли с валидацией.
     * ID не задаётся — его присваивает сервер.
     *
     * @return сконструированный объект MusicBand с id=0.
     */
    public MusicBand read() {
        String name = readRequiredString("Enter name: ");
        Long x = readLong("Enter x coordinate (max 52): ", null, 52L);
        Double y = readDouble("Enter y coordinate: ");
        Long participants = readLong("Enter number of participants (> 0): ", 1L, null);
        MusicGenre genre = readGenre();
        Studio studio = readStudio();

        return new MusicBand(name, new Coordinates(x, y), participants, genre, studio);
    }

    /**
     * Читает непустую строку.
     *
     * @param prompt приглашение для ввода.
     * @return непустая строка.
     */
    private String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  Value cannot be empty.");
        }
    }

    /**
     * Читает {@code long} с проверкой границ [min, max].
     *
     * @param prompt приглашение.
     * @param min    минимум (null = без ограничения).
     * @param max    максимум (null = без ограничения).
     * @return введённое значение.
     */
    private Long readLong(String prompt, Long min, Long max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                long val = Long.parseLong(input);
                if (min != null && val < min) { System.out.println("  Minimum: " + min); continue; }
                if (max != null && val > max) { System.out.println("  Maximum: " + max); continue; }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    /**
     * Читает {@code double}.
     *
     * @param prompt приглашение.
     * @return введённое значение.
     */
    private Double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number.");
            }
        }
    }

    /**
     * Читает жанр из перечисления {@link MusicGenre} по имени или номеру.
     *
     * @return выбранный жанр.
     */
    private MusicGenre readGenre() {
        MusicGenre[] genres = MusicGenre.values();
        String options = Arrays.stream(genres)
                .map(g -> (Arrays.asList(genres).indexOf(g) + 1) + ". " + g.name())
                .collect(Collectors.joining(", "));
        System.out.println("  Genres: " + options);

        while (true) {
            System.out.print("Enter genre (name or number): ");
            String input = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < genres.length) return genres[idx];
            } catch (NumberFormatException ignored) {}
            try {
                return MusicGenre.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid genre. Choose from: " + options);
            }
        }
    }

    /**
     * Читает данные студии (поля могут быть пустыми).
     *
     * @return объект {@link Studio}.
     */
    private Studio readStudio() {
        System.out.print("Enter studio name (or empty): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter studio address (or empty): ");
        String address = scanner.nextLine().trim();
        return new Studio(name.isEmpty() ? null : name, address.isEmpty() ? null : address);
    }
}
