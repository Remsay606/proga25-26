package ru.codehub.util;

import java.math.BigDecimal;

public class NumbParser {
    public static double parseDouble(String input) throws NumberFormatException {
        if (input == null) throw new NumberFormatException("Строка пуста");
        // Заменяем запятую на точку и убираем пробелы
        String cleaned = input.trim().replace(',', '.');
        // BigDecimal принимает Любую длину
        BigDecimal bd = new BigDecimal(cleaned);
        return bd.doubleValue();
    }

    public static long parseLong(String input) throws NumberFormatException {
        String cleaned = input.trim().replace(',', '.');
        return new BigDecimal(cleaned).longValue();
    }
}