package ru.codehub.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Основной класс модели, представляющий музыкальную группу.
 * <p>
 * Реализует {@link Serializable} для передачи между клиентом и сервером
 * в сериализованном виде через UDP.
 * Реализует {@link Comparable} для обеспечения сортировки по умолчанию (по имени).
 * </p>
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {

    private long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Long numberOfParticipants;
    private MusicGenre genre;
    private Studio studio;

    /**
     * Полный конструктор — используется при загрузке из файла.
     *
     * @param id                   уникальный идентификатор.
     * @param name                 название группы (не пустое).
     * @param coordinates          координаты (не null).
     * @param creationDate         дата создания (если null — текущая дата).
     * @param numberOfParticipants количество участников (больше 0).
     * @param genre                музыкальный жанр (не null).
     * @param studio               студия записи (не null).
     */
    public MusicBand(long id, String name, Coordinates coordinates, Date creationDate,
                     Long numberOfParticipants, MusicGenre genre, Studio studio) {
        this.id = id;
        setName(name);
        setCoordinates(coordinates);
        this.creationDate = (creationDate != null) ? creationDate : new Date();
        setNumberOfParticipants(numberOfParticipants);
        setGenre(genre);
        setStudio(studio);
    }

    /**
     * Облегчённый конструктор для создания новых групп.
     * ID устанавливается в 0, дата создания — текущая.
     *
     * @param name                 название группы.
     * @param coordinates          координаты.
     * @param numberOfParticipants количество участников.
     * @param genre                жанр.
     * @param studio               студия.
     */
    public MusicBand(String name, Coordinates coordinates, Long numberOfParticipants,
                     MusicGenre genre, Studio studio) {
        this(0, name, coordinates, new Date(), numberOfParticipants, genre, studio);
    }

    // ---- Геттеры/сеттеры ----

    /** @return уникальный идентификатор группы. */
    public long getId() { return id; }

    /** Устанавливает идентификатор (вызывается сервером). */
    public void setId(long id) { this.id = id; }

    /** @return название группы. */
    public String getName() { return name; }

    /**
     * Устанавливает название группы.
     *
     * @param name непустая строка.
     * @throws IllegalArgumentException если имя пустое или null.
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or empty");
        this.name = name;
    }

    /** @return координаты группы. */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Устанавливает координаты.
     *
     * @param coordinates объект координат (не null).
     */
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null");
        this.coordinates = coordinates;
    }

    /** @return дата создания записи. */
    public Date getCreationDate() { return creationDate; }

    /** Устанавливает дату создания (используется при обновлении). */
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    /** @return количество участников. */
    public Long getNumberOfParticipants() { return numberOfParticipants; }

    /**
     * Устанавливает количество участников.
     *
     * @param numberOfParticipants положительное число (не null).
     * @throws IllegalArgumentException если значение null или ≤ 0.
     */
    public void setNumberOfParticipants(Long numberOfParticipants) {
        if (numberOfParticipants == null || numberOfParticipants <= 0)
            throw new IllegalArgumentException("numberOfParticipants must be > 0 and not null");
        this.numberOfParticipants = numberOfParticipants;
    }

    /** @return музыкальный жанр. */
    public MusicGenre getGenre() { return genre; }

    /**
     * Устанавливает жанр.
     *
     * @param genre не null.
     */
    public void setGenre(MusicGenre genre) {
        if (genre == null) throw new IllegalArgumentException("Genre cannot be null");
        this.genre = genre;
    }

    /** @return студия записи. */
    public Studio getStudio() { return studio; }

    /**
     * Устанавливает студию.
     *
     * @param studio не null.
     */
    public void setStudio(Studio studio) {
        if (studio == null) throw new IllegalArgumentException("Studio cannot be null");
        this.studio = studio;
    }

    /**
     * Сравнивает группы по имени; при совпадении — по ID.
     * Используется в {@link java.util.PriorityQueue} для хранения и сортировки.
     */
    @Override
    public int compareTo(MusicBand other) {
        int cmp = this.name.compareTo(other.name);
        return (cmp != 0) ? cmp : Long.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicBand)) return false;
        return id == ((MusicBand) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "MusicBand{id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", genre=" + genre +
                ", studio=" + studio + '}';
    }
}
