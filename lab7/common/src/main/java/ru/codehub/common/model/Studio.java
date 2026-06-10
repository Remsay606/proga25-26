package ru.codehub.common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель студии звукозаписи.
 * Реализует {@link Serializable} для передачи по сети.
 */
public class Studio implements Serializable {

    private String name;
    private String address;

    /**
     * Создаёт объект студии.
     *
     * @param name    название студии (может быть null).
     * @param address адрес студии (может быть null).
     */
    public Studio(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /** @return название студии. */
    public String getName() { return name; }

    /** Обновляет название студии. */
    public void setName(String name) { this.name = name; }

    /** @return адрес студии. */
    public String getAddress() { return address; }

    /** Обновляет адрес студии. */
    public void setAddress(String address) { this.address = address; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Studio)) return false;
        Studio s = (Studio) o;
        return Objects.equals(name, s.name) && Objects.equals(address, s.address);
    }

    @Override
    public int hashCode() { return Objects.hash(name, address); }

    @Override
    public String toString() {
        return "Studio{name='" + name + "', address='" + address + "'}";
    }
}
