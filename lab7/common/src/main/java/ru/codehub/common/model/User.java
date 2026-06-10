package ru.codehub.common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель учётных данных пользователя, передаваемых между клиентом и сервером.
 * <p>
 * Содержит логин и пароль. Объект сериализуется и прикрепляется к каждому
 * {@link ru.codehub.common.network.CommandRequest} — согласно требованию
 * «для идентификации пользователя отправлять логин и пароль с каждым запросом».
 * </p>
 *
 * <p><b>Важно:</b> по сети передаётся пароль в открытом виде только внутри
 * сериализованного объекта; на сервере он немедленно хешируется алгоритмом
 * SHA-256 и сравнивается с хранимым в БД хешем. В открытом виде пароль нигде
 * не сохраняется.</p>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Логин пользователя (уникальный, не пустой). */
    private final String login;

    /** Пароль в открытом виде (хешируется на сервере, в БД не хранится). */
    private final String password;

    /**
     * Создаёт объект учётных данных.
     *
     * @param login    логин пользователя.
     * @param password пароль в открытом виде.
     */
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /** @return логин пользователя. */
    public String getLogin() {
        return login;
    }

    /** @return пароль в открытом виде. */
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(login, ((User) o).login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        // Пароль НИКОГДА не выводится в лог/toString
        return "User{login='" + login + "'}";
    }
}
