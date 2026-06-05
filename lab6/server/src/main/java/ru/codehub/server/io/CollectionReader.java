package ru.codehub.server.io;

import ru.codehub.common.model.MusicBand;

import java.io.IOException;
import java.util.Collection;

/**
 * Интерфейс для чтения коллекции музыкальных групп из внешнего источника.
 * Паттерн <b>Strategy</b> — позволяет подменять реализацию (CSV, JSON и др.).
 */
public interface CollectionReader {

    /**
     * Считывает коллекцию из указанного источника.
     *
     * @param source путь к файлу или другой идентификатор источника.
     * @return коллекция прочитанных объектов.
     * @throws IOException если возникла ошибка чтения.
     */
    Collection<MusicBand> read(String source) throws IOException;
}
