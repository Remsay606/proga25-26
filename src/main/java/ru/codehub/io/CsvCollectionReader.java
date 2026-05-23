package ru.codehub.io;

import ru.codehub.util.NumbParser;
import ru.codehub.model.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Класс для чтения коллекции музыкальных групп из CSV-файла.
 */
public class CsvCollectionReader implements CollectionReader {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Считывает данные из указанного файла и преобразует их в коллекцию объектов.
     * @param source путь к исходному CSV-файлу.
     * @return коллекция считанных музыкальных групп.
     * @throws IOException если возникли ошибки доступа к файлу или чтения данных.
     */
    @Override
    public Collection<MusicBand> read(String source) throws IOException {
        List<MusicBand> bands = new ArrayList<>();
        File file = new File(source);

        if (!file.exists()) {
            return bands;
        }

        if (!file.canRead()) {
            throw new IOException("No read permission for file: " + source);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            // Пропускаем заголовок
            br.readLine();
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] fields = parseCsvLine(line);
                    MusicBand band = parseFields(fields);
                    bands.add(band);
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNum + ": " + e.getMessage());
                }
            }
        }

        return bands;
    }

    /**
     * Разбирает одну строку CSV с учётом кавычек и запятых внутри полей.
     * @param line строка CSV.
     * @return массив значений полей.
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Разбирает массив строк из CSV в объект MusicBand.
     * @param fields массив значений одной строки CSV.
     * @return созданный объект музыкальной группы.
     * @throws ParseException если дата указана в некорректном формате.
     * @throws IllegalArgumentException если отсутствуют обязательные поля или данные невалидны.
     */
    private MusicBand parseFields(String[] fields) throws ParseException {
        if (fields.length < 9) {
            throw new IllegalArgumentException("Invalid CSV line: not enough fields");
        }

        long id = NumbParser.parseLong(fields[0].trim());
        String name = fields[1].trim();
        Long x = NumbParser.parseLong(fields[2].trim());
        Double y = NumbParser.parseDouble(fields[3].trim());
        Date creationDate = DATE_FORMATTER.parse(fields[4].trim());

        if (fields[5].trim().isEmpty()) {
            throw new IllegalArgumentException("numberOfParticipants cannot be empty");
        }
        Long numberOfParticipants = Long.parseLong(fields[5].trim());

        if (fields[6].trim().isEmpty()) {
            throw new IllegalArgumentException("genre cannot be empty");
        }
        MusicGenre genre = MusicGenre.valueOf(fields[6].trim());

        String studioName = fields[7].trim().isEmpty() ? null : fields[7].trim();
        String studioAddress = fields[8].trim().isEmpty() ? null : fields[8].trim();

        Coordinates coordinates = new Coordinates(x, y);
        Studio studio = new Studio(studioName, studioAddress);

        return new MusicBand(id, name, coordinates, creationDate, numberOfParticipants, genre, studio);
    }
}