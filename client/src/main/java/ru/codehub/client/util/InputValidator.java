package ru.codehub.client.util;

/**
 * Утилитный класс для валидации входных данных на стороне клиента.
 * <p>
 * Проверяет корректность аргументов перед отправкой запроса на сервер,
 * снижая нагрузку на сетевой обмен при очевидных ошибках ввода.
 * </p>
 */
public final class InputValidator {

    /** Утилитный класс — конструктор закрыт. */
    private InputValidator() {}

    /**
     * Проверяет, что строка является корректным положительным целым числом.
     *
     * @param value строка для проверки.
     * @return {@code true}, если строка — положительный long.
     */
    public static boolean isPositiveLong(String value) {
        try {
            return Long.parseLong(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Проверяет, что строка не пустая.
     *
     * @param value строка для проверки.
     * @return {@code true}, если строка не null и не пустая.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
