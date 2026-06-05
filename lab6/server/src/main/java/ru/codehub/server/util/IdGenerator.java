package ru.codehub.server.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Утилитный класс для генерации уникальных идентификаторов объектов в коллекции.
 * <p>
 * Использует {@link AtomicLong} для корректного инкремента.
 * Синхронизируется с максимальным существующим ID при загрузке коллекции из файла.
 * </p>
 */
public class IdGenerator {

    private final AtomicLong current = new AtomicLong(0);

    /**
     * Генерирует и возвращает следующий уникальный ID.
     *
     * @return новое значение идентификатора.
     */
    public long nextId() {
        return current.incrementAndGet();
    }

    /**
     * Устанавливает текущее значение счётчика.
     * Вызывается при загрузке коллекции для синхронизации с максимальным ID в файле.
     *
     * @param id значение, которое станет текущим максимумом.
     */
    public void setCurrentId(long id) {
        current.set(id);
    }

    /**
     * Возвращает текущее значение счётчика без изменения.
     *
     * @return текущий максимальный ID.
     */
    public long getCurrentId() {
        return current.get();
    }
}
