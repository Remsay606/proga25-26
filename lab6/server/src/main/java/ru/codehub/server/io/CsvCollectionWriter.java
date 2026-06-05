package ru.codehub.server.io;

import com.opencsv.CSVWriter;
import ru.codehub.common.model.MusicBand;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Реализация {@link CollectionWriter} для записи коллекции в CSV-файл.
 * <p>
 * Использует {@link java.io.FileOutputStream} + {@link OutputStreamWriter}
 * для работы с потоками вывода.
 * </p>
 */
public class CsvCollectionWriter implements CollectionWriter {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String[] HEADER = {
            "id", "name", "x", "y", "creationDate",
            "numberOfParticipants", "genre", "studioName", "studioAddress"
    };

    /**
     * Записывает коллекцию в файл.
     *
     * @param collection набор объектов для сохранения.
     * @param destination путь к файлу.
     * @throws IOException если файл недоступен для записи.
     */
    @Override
    public void write(Collection<MusicBand> collection, String destination) throws IOException {
        File file = new File(destination);
        if (file.exists() && !file.canWrite())
            throw new IOException("No write permission: " + destination);

        try (FileOutputStream fos = new FileOutputStream(destination);
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.writeNext(HEADER);
            for (MusicBand b : collection) {
                writer.writeNext(toRow(b));
            }
        }
    }

    /**
     * Преобразует объект {@link MusicBand} в строку CSV.
     *
     * @param b объект.
     * @return массив строк-полей.
     */
    private String[] toRow(MusicBand b) {
        return new String[]{
                String.valueOf(b.getId()),
                b.getName(),
                String.valueOf(b.getCoordinates().getX()),
                String.valueOf(b.getCoordinates().getY()),
                DATE_FMT.format(b.getCreationDate()),
                String.valueOf(b.getNumberOfParticipants()),
                b.getGenre().name(),
                b.getStudio().getName() != null ? b.getStudio().getName() : "",
                b.getStudio().getAddress() != null ? b.getStudio().getAddress() : ""
        };
    }
}
