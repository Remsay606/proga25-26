package ru.codehub.server.collection;

import ru.codehub.common.model.MusicBand;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс управления коллекцией объектов {@link MusicBand}.
 * <p>
 * Определяет контракт для всех операций: добавление, удаление, поиск,
 * фильтрация (через Stream API), получение метаданных коллекции.
 * </p>
 */
public interface CollectionManager {

    /**
     * Добавляет новую музыкальную группу в коллекцию.
     *
     * @param band объект для добавления.
     * @return добавленный объект с присвоенным ID.
     */
    MusicBand add(MusicBand band);

    /**
     * Удаляет музыкальную группу по ID.
     *
     * @param id уникальный идентификатор.
     * @return {@code true}, если элемент был найден и удалён.
     */
    boolean remove(long id);

    /**
     * Обновляет данные существующей группы.
     * Старый объект удаляется, новый добавляется с прежним ID и датой создания.
     *
     * @param id     ID группы, которую нужно обновить.
     * @param newBand объект с новыми данными.
     * @return {@link Optional} с обновлённым объектом или пустой, если ID не найден.
     */
    Optional<MusicBand> update(long id, MusicBand newBand);

    /** Полностью очищает коллекцию. */
    void clear();

    /**
     * Ищет группу по ID.
     *
     * @param id идентификатор.
     * @return {@link Optional} с найденным объектом или пустой.
     */
    Optional<MusicBand> getById(long id);

    /**
     * Возвращает все элементы коллекции, отсортированные по умолчанию.
     *
     * @return отсортированный список.
     */
    List<MusicBand> getAll();

    /**
     * Извлекает и удаляет первый элемент из очереди.
     *
     * @return {@link Optional} с первым элементом или пустой, если коллекция пуста.
     */
    Optional<MusicBand> pollHead();

    /**
     * Возвращает максимальный элемент по сравнению ({@link Comparable}).
     *
     * @return {@link Optional} с максимальным элементом.
     */
    Optional<MusicBand> getMax();

    /**
     * Возвращает элемент с наиболее ранней датой создания.
     *
     * @return {@link Optional} со «старейшим» элементом.
     */
    Optional<MusicBand> getMinByCreationDate();

    /**
     * Группирует элементы по имени и возвращает количество для каждого имени.
     * Использует Stream API ({@code Collectors.groupingBy}).
     *
     * @return карта имя → количество.
     */
    Map<String, Long> groupByName();

    /**
     * Фильтрует элементы, чьё название содержит указанную подстроку.
     * Использует Stream API ({@code Stream.filter}).
     *
     * @param substring подстрока для поиска.
     * @return отсортированный список совпадений.
     */
    List<MusicBand> filterByNameContains(String substring);

    /** @return текущий размер коллекции. */
    int size();

    /** @return дата и время инициализации менеджера. */
    Date getInitializationDate();

    /** @return простое имя класса коллекции (например, "PriorityQueue"). */
    String getCollectionType();

    /**
     * Возвращает копию внутренней коллекции для сохранения в файл.
     *
     * @return коллекция объектов.
     */
    Collection<MusicBand> getCollection();

    /**
     * Заменяет текущую коллекцию новыми данными.
     * Синхронизирует генератор ID с максимальным ID в переданных данных.
     *
     * @param bands набор для загрузки.
     */
    void loadCollection(Collection<MusicBand> bands);
}
