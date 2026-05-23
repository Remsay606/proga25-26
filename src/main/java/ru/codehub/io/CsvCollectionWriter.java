package ru.codehub.io;

import ru.codehub.model.MusicBand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.StringJoiner;

/**
 * Класс для записи коллекции музыкальных групп в файл формата CSV.
 */
public class CsvCollectionWriter implements CollectionWriter {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /** Заголовки столбцов в CSV файле */
    private static final String[] HEADER = {
            "id", "name", "x", "y", "creationDate",
            "numberOfParticipants", "genre", "studioName", "studioAddress"
    };

    /**
     * Записывает переданную коллекцию в указанный файл.
     * @param collection коллекция для сохранения.
     * @param destination путь к файлу назначения.
     * @throws IOException если возникли проблемы с доступом к файлу или правами на запись.
     */
    @Override
    public void write(Collection<MusicBand> collection, String destination) throws IOException {
        File file = new File(destination);
        if (file.exists() && !file.canWrite()) {
            throw new IOException("No write permission for file: " + destination);
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destination), StandardCharsets.UTF_8))) {
            bw.write(toCsvLine(HEADER));
            bw.newLine();
            for (MusicBand band : collection) {
                bw.write(toCsvLine(bandToFields(band)));
                bw.newLine();
            }
        }
    }

    /**
     * Формирует строку CSV из массива значений, оборачивая поля в кавычки при необходимости.
     * @param fields массив строковых значений полей.
     * @return строка в формате CSV.
     */
    private String toCsvLine(String[] fields) {
        StringJoiner joiner = new StringJoiner(",");
        for (String field : fields) {
            if (field == null) field = "";
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                field = "\"" + field.replace("\"", "\"\"") + "\"";
            }
            joiner.add(field);
        }
        return joiner.toString();
    }

    /**
     * Преобразует объект MusicBand в массив строк для записи в CSV.
     * @param band объект музыкальной группы.
     * @return массив строк, представляющий поля объекта.
     */
    private String[] bandToFields(MusicBand band) {
        return new String[]{
                String.valueOf(band.getId()),
                band.getName(),
                String.valueOf(band.getCoordinates().getX()),
                String.valueOf(band.getCoordinates().getY()),
                DATE_FORMATTER.format(band.getCreationDate()),
                String.valueOf(band.getNumberOfParticipants()),
                band.getGenre().name(),
                band.getStudio().getName() != null ? band.getStudio().getName() : "",
                band.getStudio().getAddress() != null ? band.getStudio().getAddress() : ""
        };
    }
}