package ru.codehub.collection;

import ru.codehub.model.MusicBand;
import ru.codehub.util.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции музыкальных групп.
 * Реализует интерфейс CollectionManager, используя PriorityQueue для хранения элементов.
 *
 */
public class MusicBandCollectionManager implements CollectionManager {
    /** Хранилище объектов музыкальных групп */
    private final PriorityQueue<MusicBand> collection;
    /** Генератор уникальных идентификаторов для элементов коллекции */
    private final IdGenerator idGenerator;
    /** Дата и время инициализации менеджера коллекции */
    private final Date initializationDate;

    /**
     * Конструктор инициализирует пустую приоритетную очередь и фиксирует время создания.
     * @param idGenerator Объект, отвечающий за генерацию ID.
     *
     */
    public MusicBandCollectionManager(IdGenerator idGenerator) {
        this.collection = new PriorityQueue<>();
        this.idGenerator = idGenerator;
        this.initializationDate = new Date();
    }

    /**
     * Добавляет новую музыкальную группу в коллекцию.
     * @param band Объект MusicBand для добавления.
     * @return Добавленный объект.
     *
     */
    @Override
    public MusicBand add(MusicBand band) {
        collection.add(band);
        return band;
    }

    /**
     * Удаляет музыкальную группу из коллекции по её уникальному номеру (ID).
     * @param id Уникальный идентификатор группы.
     * @return true, если элемент был найден и удален, иначе false.
     *
     */
    @Override
    public boolean remove(long id) {
        return collection.removeIf(band -> band.getId() == id);
    }

    /**
     * Обновляет данные музыкальной группы. Старый объект удаляется, новый добавляется
     * с сохранением прежнего ID и даты создания.
     * @param id ID группы, которую нужно обновить.
     * @param newBand Объект с новыми характеристиками.
     * @return Optional с обновленным объектом или пустой Optional, если ID не найден.
     *
     */
    @Override
    public Optional<MusicBand> update(long id, MusicBand newBand) {
        Optional<MusicBand> existing = getById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        collection.remove(existing.get());
        newBand.setId(id);
        newBand.setCreationDate(existing.get().getCreationDate());
        collection.add(newBand);
        return Optional.of(newBand);
    }

    /**
     * Полностью очищает коллекцию от всех элементов.
     *
     */
    @Override
    public void clear() {
        collection.clear();
    }

    /**
     * Ищет музыкальную группу по её ID.
     * @param id Идентификатор для поиска.
     * @return Optional с найденной группой или пустой, если совпадений нет.
     *
     */
    @Override
    public Optional<MusicBand> getById(long id) {
        return collection.stream()
                .filter(band -> band.getId() == id)
                .findFirst();
    }

    /**
     * Возвращает список всех групп в коллекции, отсортированный по умолчанию.
     * @return Список объектов MusicBand.
     *
     */
    @Override
    public List<MusicBand> getAll() {
        return collection.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Извлекает и удаляет первый элемент из очереди (голову очереди).
     * @return Optional с первым элементом или пустой, если коллекция пуста.
     *
     */
    @Override
    public Optional<MusicBand> pollHead() {
        return Optional.ofNullable(collection.poll());
    }

    /**
     * Возвращает самый большой элемент коллекции (согласно правилам сравнения MusicBand).
     * @return Optional с "максимальной" группой.
     *
     */
    @Override
    public Optional<MusicBand> getMax() {
        return collection.stream()
                .max(Comparator.naturalOrder());
    }

    /**
     * Находит музыкальную группу с самой ранней датой создания.
     * @return Optional с самой первой созданной группой в коллекции.
     *
     */
    @Override
    public Optional<MusicBand> getMinByCreationDate() {
        return collection.stream()
                .min(Comparator.comparing(MusicBand::getCreationDate));
    }

    /**
     * Группирует элементы по их именам и подсчитывает количество групп для каждого имени.
     * @return Карта (Map), где ключ — название группы, а значение — количество таких названий.
     *
     */
    @Override
    public Map<String, Long> groupByName() {
        return collection.stream()
                .collect(Collectors.groupingBy(MusicBand::getName, Collectors.counting()));
    }

    /**
     * Фильтрует коллекцию, оставляя только те группы, чье название содержит указанную подстроку.
     * @param substring Текст для поиска в названиях групп.
     * @return Отсортированный список подходящих музыкальных групп.
     *
     */
    @Override
    public List<MusicBand> filterByNameContains(String substring) {
        return collection.stream()
                .filter(band -> band.getName().contains(substring))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Возвращает текущее количество элементов в коллекции.
     * @return Размер коллекции.
     *
     */
    @Override
    public int size() {
        return collection.size();
    }

    /**
     * Возвращает дату создания (инициализации) данного менеджера.
     * @return Объект даты инициализации.
     *
     */
    @Override
    public Date getInitializationDate() {
        return initializationDate;
    }

    /**
     * Позволяет узнать техническое имя типа коллекции (в данном случае PriorityQueue).
     * @return Имя класса коллекции.
     *
     */
    @Override
    public String getCollectionType() {
        return PriorityQueue.class.getSimpleName();
    }

    /**
     * Возвращает текущее состояние коллекции в виде списка.
     * @return Коллекция всех музыкальных групп.
     *
     */
    @Override
    public Collection<MusicBand> getCollection() {
        return new ArrayList<>(collection);
    }

    /**
     * Заменяет текущую коллекцию новыми данными и настраивает генератор ID
     * на следующее свободное значение.
     * @param bands Набор музыкальных групп для загрузки в менеджер.
     *
     */
    @Override
    public void loadCollection(Collection<MusicBand> bands) {
        collection.clear();
        long maxId = 0;
        for (MusicBand band : bands) {
            if (band.getId() > maxId) {
                maxId = band.getId();
            }
            collection.add(band);
        }
        idGenerator.setCurrentId(maxId);
    }
}