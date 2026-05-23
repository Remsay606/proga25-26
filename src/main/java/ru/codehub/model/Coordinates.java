package ru.codehub.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Класс, представляющий координаты X и Y.
 */
public class Coordinates {
    /** Максимально допустимое значение для координаты X */
    private static final long MAX_X = 52;

    private Long x;
    private Double y;

    public Coordinates(Long x, Double y) {
        setX(x);
        setY(y);
    }

    public Long getX() {
        return x;
    }

    /**
     * Устанавливает координату X с проверкой ограничений.
     * @param x значение координаты (не null, максимум 52).
     */
    public void setX(Long x) {
        if (x == null) {
            throw new IllegalArgumentException("X coordinate cannot be null");
        }
        if (x > MAX_X) {
            throw new IllegalArgumentException("X coordinate cannot be greater than " + MAX_X);
        }
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    /**
     * Устанавливает координату Y.
     * @param y значение координаты (не null).
     */
    public void setY(Double y) {
        if (y == null) {
            throw new IllegalArgumentException("Y coordinate cannot be null");
        }
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // В классе Coordinates.java
    @Override
    public String toString() {
        return String.format("Coordinates[x=%d, y=%.15f]", x, y);
    }
}