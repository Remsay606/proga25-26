package ru.codehub.model;

import java.util.Objects;

/**
 * Модель студии звукозаписи, содержащая название и адрес.
 */
public class Studio {
    private String name;
    private String address;

    /**
     * Конструктор для создания объекта студии.
     * @param name название студии.
     * @param address физический адрес студии.
     */
    public Studio(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    /**
     * Обновляет название студии.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Обновляет адрес студии.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Studio studio = (Studio) o;
        return Objects.equals(name, studio.name) && Objects.equals(address, studio.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public String toString() {
        return "Studio{name='" + name + "', address='" + address + "'}";
    }
}