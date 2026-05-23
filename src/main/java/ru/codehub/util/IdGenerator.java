package ru.codehub.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Утилитный класс для генерации уникальных идентификаторов.
 * Использует AtomicLong для обеспечения потокобезопасности при инкременте.
 */
public class IdGenerator {
    private final AtomicLong currentId;

    /**
     * Инициализирует генератор со значения 0.
     */
    public IdGenerator() {
        this.currentId = new AtomicLong(0);
    }

    /**
     * Генерирует и возвращает следующий уникальный ID.
     * @return новое значение идентификатора.
     */
    public long nextId() {
        return currentId.incrementAndGet();
    }

    /**
     * Устанавливает текущее значение счетчика.
     * Используется при загрузке коллекции для синхронизации с максимальным существующим ID.
     * @param id значение, которое станет текущим максимумом.
     */
    public void setCurrentId(long id) {
        currentId.set(id);
    }

    /**
     * Возвращает текущее значение счетчика без его изменения.
     */
    public long getCurrentId() {
        return currentId.get();
    }
}