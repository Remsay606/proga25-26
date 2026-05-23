package ru.codehub.io;

import ru.codehub.model.MusicBand;

import java.io.IOException;
import java.util.Collection;

/**
 * Интерфейс для компонентов, обеспечивающих чтение данных коллекции из внешнего источника.
 */
public interface CollectionReader {
    /**
     * Считывает данные и преобразует их в коллекцию объектов {@link MusicBand}.
     * @param source путь к файлу или ресурс, из которого производится чтение данных.
     * @return коллекция объектов музыкальных групп.
     * @throws IOException если в процессе чтения или доступа к источнику произошла ошибка.
     */
    Collection<MusicBand> read(String source) throws IOException;
}