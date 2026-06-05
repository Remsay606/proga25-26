package ru.codehub.server.io;

import ru.codehub.common.model.MusicBand;

import java.io.IOException;
import java.util.Collection;

/**
 * Интерфейс для записи коллекции музыкальных групп во внешнее хранилище.
 * Паттерн <b>Strategy</b> — позволяет подменять формат записи.
 */
public interface CollectionWriter {

    /**
     * Записывает коллекцию по указанному пути.
     *
     * @param collection коллекция для сохранения.
     * @param destination путь к файлу назначения.
     * @throws IOException если возникла ошибка записи.
     */
    void write(Collection<MusicBand> collection, String destination) throws IOException;
}
