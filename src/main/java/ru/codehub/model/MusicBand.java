package ru.codehub.model;

import java.util.Date;
import java.util.Objects;

/**
 * Основной класс модели, представляющий музыкальную группу.
 * Реализует интерфейс Comparable для обеспечения сортировки (по умолчанию — по имени).
 */
public class MusicBand implements Comparable<MusicBand> {
    private long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Long numberOfParticipants;
    private MusicGenre genre;
    private Studio studio;

    /**
     * Полный конструктор для создания объекта со всеми полями.
     * Используется преимущественно при загрузке данных из файла.
     */
    public MusicBand(long id, String name, Coordinates coordinates, Date creationDate,
                     Long numberOfParticipants, MusicGenre genre, Studio studio) {
        this.id = id;
        setName(name);
        setCoordinates(coordinates);
        this.creationDate = creationDate != null ? creationDate : new Date();
        setNumberOfParticipants(numberOfParticipants);
        setGenre(genre);
        setStudio(studio);
    }

    /**
     * Облегченный конструктор для создания новых групп.
     * ID инициализируется нулем, а дата создания — текущим временем.
     */
    public MusicBand(String name, Coordinates coordinates, Long numberOfParticipants,
                     MusicGenre genre, Studio studio) {
        this(0, name, coordinates, new Date(), numberOfParticipants, genre, studio);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя группы.
     * @throws IllegalArgumentException если имя пустое или равно null.
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Устанавливает координаты местоположения группы.
     * @throws IllegalArgumentException если объект координат равен null.
     */
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    /**
     * Устанавливает количество участников в группе.
     * @param numberOfParticipants значение должно быть больше 0.
     * @throws IllegalArgumentException если значение null или меньше/равно нулю.
     */
    public void setNumberOfParticipants(Long numberOfParticipants) {
        if (numberOfParticipants == null) {
            throw new IllegalArgumentException("Number of participants cannot be null");
        }
        if (numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Number of participants must be greater than 0");
        }
        this.numberOfParticipants = numberOfParticipants;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    /**
     * Устанавливает жанр музыки.
     * @throws IllegalArgumentException если жанр не указан.
     */
    public void setGenre(MusicGenre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        this.genre = genre;
    }

    public Studio getStudio() {
        return studio;
    }

    /**
     * Устанавливает студию звукозаписи.
     * @throws IllegalArgumentException если объект студии равен null.
     */
    public void setStudio(Studio studio) {
        if (studio == null) {
            throw new IllegalArgumentException("Studio cannot be null");
        }
        this.studio = studio;
    }

    /**
     * Сравнивает две группы по имени. При совпадении имен сравнение идет по ID.
     */
    @Override
    public int compareTo(MusicBand other) {
        int result = this.name.compareTo(other.name);
        if (result == 0) {
            result = Long.compare(this.id, other.id);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBand musicBand = (MusicBand) o;
        return id == musicBand.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MusicBand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", genre=" + genre +
                ", studio=" + studio +
                '}';
    }
}