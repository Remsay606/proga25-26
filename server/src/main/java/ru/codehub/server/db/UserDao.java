package ru.codehub.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO для работы с таблицей {@code users}.
 *
 * <p><b>Роли:</b> USER (обычный пользователь) и ADMIN.</p>
 * <p>Администратор может:
 * <ul>
 *   <li>выдавать роль ADMIN другим пользователям ({@link #promoteToAdmin});</li>
 *   <li>понижать ADMIN до USER ({@link #demoteToUser});</li>
 *   <li>модифицировать чужие объекты (проверка в хендлерах).</li>
 * </ul>
 * </p>
 */
public class UserDao {

    private final DatabaseManager db;

    public UserDao(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Проверяет, существует ли пользователь.
     */
    public boolean exists(String login) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE login = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /**
     * Регистрирует нового пользователя с ролью USER.
     *
     * @return false если логин уже занят.
     */
    public boolean register(String login, String passwordHash) throws SQLException {
        if (exists(login)) return false;
        String sql = "INSERT INTO users (login, password_hash, role) VALUES (?, ?, 'USER')";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Регистрирует администратора (вызывается только из инициализации при старте).
     *
     * @return false если логин уже занят.
     */
    public boolean registerAdmin(String login, String passwordHash) throws SQLException {
        if (exists(login)) return false;
        String sql = "INSERT INTO users (login, password_hash, role) VALUES (?, ?, 'ADMIN')";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Проверяет логин/хеш пароля.
     */
    public boolean checkCredentials(String login, String passwordHash) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE login = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String stored = rs.getString("password_hash");
                return stored != null && stored.equals(passwordHash);
            }
        }
    }

    /**
     * Возвращает роль пользователя: "USER", "ADMIN" или null если не найден.
     */
    public String getRole(String login) throws SQLException {
        String sql = "SELECT role FROM users WHERE login = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString("role");
            }
        }
    }

    /**
     * Повышает пользователя до ADMIN.
     *
     * @param login логин пользователя для повышения.
     * @return false если пользователь не найден или уже ADMIN.
     */
    public boolean promoteToAdmin(String login) throws SQLException {
        if (!exists(login)) return false;
        String current = getRole(login);
        if ("ADMIN".equals(current)) return false;
        String sql = "UPDATE users SET role = 'ADMIN' WHERE login = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Понижает ADMIN до USER.
     *
     * @param login      логин для понижения.
     * @param callerLogin логин того кто выполняет — нельзя понизить самого себя.
     * @return результат с пояснением.
     */
    public DemoteResult demoteToUser(String login, String callerLogin) throws SQLException {
        if (login.equals(callerLogin)) {
            return DemoteResult.fail("You cannot demote yourself");
        }
        if (!exists(login)) {
            return DemoteResult.fail("User '" + login + "' does not exist");
        }
        String current = getRole(login);
        if (!"ADMIN".equals(current)) {
            return DemoteResult.fail("User '" + login + "' is not an admin");
        }
        String sql = "UPDATE users SET role = 'USER' WHERE login = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.executeUpdate();
            return DemoteResult.ok("User '" + login + "' demoted to USER");
        }
    }

    /** Результат операции понижения. */
    public static class DemoteResult {
        public final boolean success;
        public final String message;
        private DemoteResult(boolean success, String message) {
            this.success = success; this.message = message;
        }
        public static DemoteResult ok(String msg)   { return new DemoteResult(true, msg); }
        public static DemoteResult fail(String msg) { return new DemoteResult(false, msg); }
    }
}
