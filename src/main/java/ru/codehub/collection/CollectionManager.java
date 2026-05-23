package ru.codehub.collection;

import ru.codehub.model.MusicBand;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс для управления коллекцией объектов типа MusicBand.
 * Содержит методы для модификации, поиска и получения информации о коллекции.
 */
public interface CollectionManager {
    /**
     * Добавляет новую музыкальную группу в коллекцию.
     * * @param band Объект музыкальной группы для добавления.
     * @return Добавленный объект.
     */
    MusicBand add(MusicBand band);

    /**
     * Удаляет музыкальную группу из коллекции по её ID.
     * * @param id Уникальный идентификатор группы.
     * @return true, если удаление прошло успешно, иначе false.
     */
    boolean remove(long id);

    /**
     * Обновляет данные существующей музыкальной группы по её ID.
     * * @param id Уникальный идентификатор группы.
     * @param band Объект с новыми данными.
     * @return Optional с обновленным объектом, если он найден, иначе пустой Optional.
     */
    Optional<MusicBand> update(long id, MusicBand band);

    /**
     * Полностью очищает коллекцию.
     */
    void clear();

    /**
     * Ищет музыкальную группу по её ID.
     * * @param id Уникальный идентификатор.
     * @return Optional с найденным объектом или пустой Optional.
     */
    Optional<MusicBand> getById(long id);

    /**
     * Возвращает список всех элементов коллекции.
     * * @return Список всех музыкальных групп.
     */
    List<MusicBand> getAll();

    /**
     * Извлекает и удаляет первый элемент коллекции (голову).
     * * @return Optional с первым элементом или пустой Optional, если коллекция пуста.
     */
    Optional<MusicBand> pollHead();

    /**
     * Находит элемент с максимальным значением (согласно сравнению объектов).
     * * @return Optional с максимальным элементом.
     */
    Optional<MusicBand> getMax();

    /**
     * Находит элемент с минимальной датой создания.
     * * @return Optional с самым "старым" элементом коллекции.
     */
    Optional<MusicBand> getMinByCreationDate();

    /**
     * Группирует элементы по имени и подсчитывает количество в каждой группе.
     * * @return Карта (Map), где ключ — имя, а значение — количество повторений.
     */
    Map<String, Long> groupByName();

    /**
     * Фильтрует элементы, чье имя содержит указанную подстроку.
     * * @param substring Строка для поиска в именах.
     * @return Список подходящих элементов.
     */
    List<MusicBand> filterByNameContains(String substring);

    /**
     * Возвращает количество элементов в коллекции.
     * * @return Текущий размер коллекции.
     */
    int size();

    /**
     * Возвращает дату инициализации коллекции.
     * * @return Объект Date — время создания/загрузки коллекции.
     */
    Date getInitializationDate();

    /**
     * Возвращает тип данных, хранящихся в коллекции.
     * * @return Строковое представление типа коллекции.
     */
    String getCollectionType();

    /**
     * Возвращает саму коллекцию объектов.
     * * @return Ссылка на коллекцию MusicBand.
     */
    Collection<MusicBand> getCollection();

    /**
     * Загружает данные в коллекцию из внешнего источника.
     * * @param bands Набор музыкальных групп для загрузки.
     */
    void loadCollection(Collection<MusicBand> bands);
}