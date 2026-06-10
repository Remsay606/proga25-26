package ru.codehub.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Менеджер подключения к PostgreSQL.
 *
 * <h3>Схема БД:</h3>
 * <ul>
 *   <li>{@code users} — логин, SHA-256 хеш пароля, роль (USER / ADMIN).</li>
 *   <li>{@code music_bands} — объекты коллекции; id через BIGSERIAL.</li>
 * </ul>
 */
public class DatabaseManager {

    private final DbConfig config;
    private Connection connection;

    public DatabaseManager(DbConfig config) {
        this.config = config;
    }

    public void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found", e);
        }
        connection = DriverManager.getConnection(
                config.getUrl(), config.getUser(), config.getPassword());
        connection.setAutoCommit(true);
        initSchema();
    }

    private void initSchema() throws SQLException {
        // Тип роли
        String createRoleType =
                "DO $$ BEGIN " +
                "  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN " +
                "    CREATE TYPE user_role AS ENUM ('USER', 'ADMIN'); " +
                "  END IF; " +
                "END $$";

        // Таблица пользователей с ролью
        String createUsers =
                "CREATE TABLE IF NOT EXISTS users (" +
                "  login         VARCHAR(255) PRIMARY KEY," +
                "  password_hash VARCHAR(64)  NOT NULL," +
                "  role          VARCHAR(10)  NOT NULL DEFAULT 'USER'" +
                ")";

        // Таблица коллекции
        String createBands =
                "CREATE TABLE IF NOT EXISTS music_bands (" +
                "  id             BIGSERIAL PRIMARY KEY," +
                "  name           VARCHAR(255) NOT NULL CHECK (length(name) > 0)," +
                "  coord_x        BIGINT NOT NULL CHECK (coord_x <= 52)," +
                "  coord_y        DOUBLE PRECISION NOT NULL," +
                "  creation_date  TIMESTAMP NOT NULL," +
                "  participants   BIGINT NOT NULL CHECK (participants > 0)," +
                "  genre          VARCHAR(64) NOT NULL," +
                "  studio_name    VARCHAR(255)," +
                "  studio_address VARCHAR(255)," +
                "  owner_login    VARCHAR(255) NOT NULL REFERENCES users(login)" +
                ")";

        // Добавить колонку role если её нет (для существующих БД)
        String addRoleColumn =
                "DO $$ BEGIN " +
                "  IF NOT EXISTS (" +
                "    SELECT 1 FROM information_schema.columns " +
                "    WHERE table_name='users' AND column_name='role'" +
                "  ) THEN " +
                "    ALTER TABLE users ADD COLUMN role VARCHAR(10) NOT NULL DEFAULT 'USER'; " +
                "  END IF; " +
                "END $$";

        try (Statement st = connection.createStatement()) {
            st.execute(createUsers);
            st.execute(addRoleColumn);
            st.execute(createBands);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            System.err.println("[DB] Error closing: " + e.getMessage());
        }
    }
}
