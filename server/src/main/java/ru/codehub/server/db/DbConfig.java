package ru.codehub.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Загрузчик конфигурации подключения к БД и начального администратора.
 *
 * <p>Формат файла {@code db.cfg}:</p>
 * <pre>
 * db.url=jdbc:postgresql://pg:5432/studs
 * db.user=s504568
 * db.password=secret
 *
 * # Первичный администратор (создаётся при первом запуске, если нет в БД)
 * admin.login=admin
 * admin.password=admin123
 * </pre>
 */
public class DbConfig {

    private final String url;
    private final String user;
    private final String password;
    private final String adminLogin;
    private final String adminPassword;

    public DbConfig(String url, String user, String password,
                    String adminLogin, String adminPassword) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }

    /**
     * Загружает конфигурацию из внешнего файла.
     *
     * @param path путь к конфиг-файлу.
     * @return объект конфигурации.
     * @throws IOException если файл не найден или не содержит нужных ключей.
     */
    public static DbConfig fromFile(String path) throws IOException {
        Properties props = new Properties();
        Path p = Path.of(path);
        if (!Files.exists(p)) {
            throw new IOException("Config file not found: " + path);
        }
        if (!Files.isReadable(p)) {
            throw new IOException("Config file is not readable: " + path);
        }
        try (InputStream in = Files.newInputStream(p)) {
            props.load(in);
        }
        String url      = require(props, "db.url", path);
        String user     = require(props, "db.user", path);
        String password = props.getProperty("db.password", "");
        // admin — не обязательные поля, дефолты если не заданы
        String adminLogin    = props.getProperty("admin.login", "admin").trim();
        String adminPassword = props.getProperty("admin.password", "admin").trim();
        return new DbConfig(url, user, password, adminLogin, adminPassword);
    }

    private static String require(Properties props, String key, String path) throws IOException {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IOException("Missing required key '" + key + "' in config: " + path);
        }
        return value.trim();
    }

    public String getUrl()           { return url; }
    public String getUser()          { return user; }
    public String getPassword()      { return password; }
    public String getAdminLogin()    { return adminLogin; }
    public String getAdminPassword() { return adminPassword; }
}
