package ru.codehub.server.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилита хеширования паролей алгоритмом <b>SHA-256</b>.
 * <p>
 * Согласно требованию «пароли при хранении хешировать алгоритмом SHA-256».
 * Пароль в открытом виде в базе данных не хранится — только его хеш.
 * </p>
 *
 * <p>Результат — шестнадцатеричная строка длиной 64 символа.</p>
 */
public final class PasswordHasher {

    /** Утилитный класс. */
    private PasswordHasher() {}

    /**
     * Вычисляет SHA-256 хеш пароля.
     *
     * @param password пароль в открытом виде (может быть пустым, но не null).
     * @return hex-строка хеша (64 символа в нижнем регистре).
     * @throws IllegalStateException если алгоритм SHA-256 недоступен в среде (не должно случаться).
     */
    public static String hash(String password) {
        if (password == null) password = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
