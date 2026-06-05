package ru.codehub.server.io;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ru.codehub.common.model.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Реализация {@link CollectionReader} для чтения из CSV-файла.
 * <p>
 * Использует {@link BufferedInputStream} + {@link ObjectInputStream} цепочку
 * (для демонстрации работы с потоками ввода-вывода).
 * Парсинг строк выполняется через библиотеку opencsv.
 * </p>
 */
public class CsvCollectionReader implements CollectionReader {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Считывает все строки из CSV и преобразует их в объекты {@link MusicBand}.
     * Некорректные строки пропускаются с выводом предупреждения в stderr.
     *
     * @param source путь к CSV-файлу.
     * @return коллекция прочитанных объектов (может быть пустой).
     * @throws IOException если файл недоступен для чтения.
     */
    @Override
    public Collection<MusicBand> read(String source) throws IOException {
        List<MusicBand> bands = new ArrayList<>();
        File file = new File(source);
        if (!file.exists()) return bands;
        if (!file.canRead()) throw new IOException("No read permission: " + source);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             CSVReader csv = new CSVReaderBuilder(new InputStreamReader(bis))
                     .withSkipLines(1).build()) {

            String[] row;
            while ((row = csv.readNext()) != null) {
                try {
                    bands.add(parseRow(row));
                } catch (Exception e) {
                    System.err.println("[WARN] Skipping invalid CSV row: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("CSV read error: " + e.getMessage(), e);
        }
        return bands;
    }

    /**
     * Разбирает одну строку CSV в объект {@link MusicBand}.
     *
     * @param f массив полей из одной строки.
     * @return объект MusicBand.
     * @throws ParseException           если дата в некорректном формате.
     * @throws IllegalArgumentException если обязательные поля отсутствуют.
     */
    private MusicBand parseRow(String[] f) throws ParseException {
        if (f.length < 9) throw new IllegalArgumentException("Not enough fields: " + f.length);
        long id = Long.parseLong(f[0].trim());
        String name = f[1].trim();
        Long x = Long.parseLong(f[2].trim());
        Double y = Double.parseDouble(f[3].trim());
        Date date = DATE_FMT.parse(f[4].trim());
        Long participants = Long.parseLong(f[5].trim());
        MusicGenre genre = MusicGenre.valueOf(f[6].trim());
        String studioName = f[7].trim().isEmpty() ? null : f[7].trim();
        String studioAddr = f[8].trim().isEmpty() ? null : f[8].trim();
        return new MusicBand(id, name, new Coordinates(x, y), date,
                participants, genre, new Studio(studioName, studioAddr));
    }
}
