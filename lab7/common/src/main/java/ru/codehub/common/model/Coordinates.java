package ru.codehub.common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий координаты X и Y музыкальной группы.
 * Реализует {@link Serializable} для передачи по сети через UDP.
 */
public class Coordinates implements Serializable {

    /** Максимально допустимое значение для координаты X. */
    private static final long MAX_X = 52;

    private Long x;
    private Double y;

    /**
     * Создаёт объект координат с проверкой допустимых значений.
     *
     * @param x координата X (не null, не больше 52).
     * @param y координата Y (не null).
     * @throws IllegalArgumentException если значения нарушают ограничения.
     */
    public Coordinates(Long x, Double y) {
        setX(x);
        setY(y);
    }

    /** @return текущее значение координаты X. */
    public Long getX() {
        return x;
    }

    /**
     * Устанавливает координату X.
     *
     * @param x значение (не null, максимум {@value MAX_X}).
     * @throws IllegalArgumentException если значение нарушает ограничения.
     */
    public void setX(Long x) {
        if (x == null) throw new IllegalArgumentException("X coordinate cannot be null");
        if (x > MAX_X) throw new IllegalArgumentException("X coordinate cannot be greater than " + MAX_X);
        this.x = x;
    }

    /** @return текущее значение координаты Y. */
    public Double getY() {
        return y;
    }

    /**
     * Устанавливает координату Y.
     *
     * @param y значение (не null).
     * @throws IllegalArgumentException если значение равно null.
     */
    public void setY(Double y) {
        if (y == null) throw new IllegalArgumentException("Y coordinate cannot be null");
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Coordinates[x=%d, y=%.2f]", x, y);
    }
}
