package ru.codehub.server.collection;

import ru.codehub.common.model.MusicBand;
import ru.codehub.server.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация {@link CollectionManager} на основе {@link PriorityQueue}.
 * <p>
 * Очередь с приоритетом хранит элементы в порядке, определяемом
 * {@link MusicBand#compareTo(MusicBand)} (по имени).
 * Все операции фильтрации и агрегации реализованы через <b>Stream API</b>
 * с лямбда-выражениями.
 * </p>
 */
public class MusicBandCollectionManager implements CollectionManager {

    /** Основное хранилище коллекции. */
    private final PriorityQueue<MusicBand> collection;

    /** Генератор уникальных идентификаторов. */
    private final IdGenerator idGenerator;

    /** Момент создания менеджера (фиксируется при старте сервера). */
    private final Date initializationDate;

    /**
     * Создаёт менеджер с пустой коллекцией.
     *
     * @param idGenerator объект-генератор идентификаторов.
     */
    public MusicBandCollectionManager(IdGenerator idGenerator) {
        this.collection = new PriorityQueue<>();
        this.idGenerator = idGenerator;
        this.initializationDate = new Date();
    }

    /**
     * Добавляет группу в коллекцию, присваивая ей следующий свободный ID.
     *
     * @param band объект для добавления.
     * @return добавленный объект с присвоенным ID.
     */
    @Override
    public MusicBand add(MusicBand band) {
        band.setId(idGenerator.nextId());
        collection.add(band);
        return band;
    }

    /**
     * Удаляет элемент по ID с помощью {@code removeIf} и лямбда-выражения.
     *
     * @param id идентификатор группы.
     * @return {@code true}, если элемент был найден и удалён.
     */
    @Override
    public boolean remove(long id) {
        return collection.removeIf(band -> band.getId() == id);
    }

    /**
     * Обновляет существующую запись: удаляет старую, добавляет новую
     * с сохранением прежнего ID и даты создания.
     *
     * @param id      ID группы.
     * @param newBand новые данные.
     * @return {@link Optional} с обновлённым объектом или пустой, если не найден.
     */
    @Override
    public Optional<MusicBand> update(long id, MusicBand newBand) {
        Optional<MusicBand> existing = getById(id);
        if (existing.isEmpty()) return Optional.empty();

        collection.remove(existing.get());
        newBand.setId(id);
        newBand.setCreationDate(existing.get().getCreationDate());
        collection.add(newBand);
        return Optional.of(newBand);
    }

    /** Удаляет все элементы из коллекции. */
    @Override
    public void clear() {
        collection.clear();
    }

    /**
     * Ищет элемент по ID через {@code Stream.filter}.
     *
     * @param id идентификатор.
     * @return {@link Optional} с найденным объектом.
     */
    @Override
    public Optional<MusicBand> getById(long id) {
        return collection.stream()
                .filter(b -> b.getId() == id)
                .findFirst();
    }

    /**
     * Возвращает список всех элементов, отсортированных через {@code Stream.sorted}.
     *
     * @return отсортированный список.
     */
    @Override
    public List<MusicBand> getAll() {
        return collection.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Извлекает голову очереди (наименьший элемент по компаратору).
     *
     * @return {@link Optional} с извлечённым элементом.
     */
    @Override
    public Optional<MusicBand> pollHead() {
        return Optional.ofNullable(collection.poll());
    }

    /**
     * Находит максимальный элемент через {@code Stream.max}.
     *
     * @return {@link Optional} с максимальным элементом.
     */
    @Override
    public Optional<MusicBand> getMax() {
        return collection.stream().max(Comparator.naturalOrder());
    }

    /**
     * Находит элемент с наименьшей датой создания через {@code Stream.min}.
     *
     * @return {@link Optional} с самым «старым» элементом.
     */
    @Override
    public Optional<MusicBand> getMinByCreationDate() {
        return collection.stream()
                .min(Comparator.comparing(MusicBand::getCreationDate));
    }

    /**
     * Группирует элементы по имени через {@code Collectors.groupingBy}.
     *
     * @return карта: имя → количество.
     */
    @Override
    public Map<String, Long> groupByName() {
        return collection.stream()
                .collect(Collectors.groupingBy(MusicBand::getName, Collectors.counting()));
    }

    /**
     * Фильтрует элементы через {@code Stream.filter} с лямбдой на {@code contains}.
     *
     * @param substring подстрока для поиска в имени.
     * @return отсортированный список совпадений.
     */
    @Override
    public List<MusicBand> filterByNameContains(String substring) {
        return collection.stream()
                .filter(b -> b.getName().contains(substring))
                .sorted()
                .collect(Collectors.toList());
    }

    /** @return текущий размер коллекции. */
    @Override
    public int size() {
        return collection.size();
    }

    /** @return дата инициализации менеджера. */
    @Override
    public Date getInitializationDate() {
        return initializationDate;
    }

    /** @return "PriorityQueue". */
    @Override
    public String getCollectionType() {
        return PriorityQueue.class.getSimpleName();
    }

    /** @return копия внутренней коллекции (для сохранения в файл). */
    @Override
    public Collection<MusicBand> getCollection() {
        return new ArrayList<>(collection);
    }

    /**
     * Загружает переданные данные в коллекцию, обновляя генератор ID.
     *
     * @param bands данные для загрузки.
     */
    @Override
    public void loadCollection(Collection<MusicBand> bands) {
        collection.clear();
        long maxId = 0;
        for (MusicBand b : bands) {
            if (b.getId() > maxId) maxId = b.getId();
            collection.add(b);
        }
        idGenerator.setCurrentId(maxId);
    }
}
