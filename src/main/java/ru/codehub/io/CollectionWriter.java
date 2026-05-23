package ru.codehub.io;

import ru.codehub.model.MusicBand;

import java.io.IOException;
import java.util.Collection;

/**
 * Интерфейс для компонентов, отвечающих за сохранение коллекции музыкальных групп во внешнее хранилище.
 */
public interface CollectionWriter {
    /**
     * Записывает переданную коллекцию в указанное место назначения (например, в файл).
     * @param collection коллекция объектов {@link MusicBand} для сохранения.
     * @param destination строка, определяющая путь к файлу или ресурс для записи.
     * @throws IOException если в процессе записи произошла ошибка ввода-вывода.
     */
    void write(Collection<MusicBand> collection, String destination) throws IOException;
}