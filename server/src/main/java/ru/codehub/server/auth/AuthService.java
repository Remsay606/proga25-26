package ru.codehub.server.auth;

import ru.codehub.common.model.User;
import ru.codehub.server.db.DbConfig;
import ru.codehub.server.db.UserDao;

import java.sql.SQLException;

/**
 * Сервис аутентификации, регистрации и управления ролями.
 *
 * <p>При создании вызывает {@link #initDefaultAdmin(DbConfig)} — создаёт
 * первичного администратора из конфига если его ещё нет в БД.</p>
 *
 * <p>Метод {@link #getRole(String)} используется диспетчером для передачи
 * роли в хендлеры команд.</p>
 */
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao, DbConfig config) {
        this.userDao = userDao;
        initDefaultAdmin(config);
    }

    /**
     * Создаёт администратора из конфига при первом запуске.
     * Если логин уже занят — пропускает (не перезаписывает пароль).
     */
    private void initDefaultAdmin(DbConfig config) {
        try {
            String hash = PasswordHasher.hash(config.getAdminPassword());
            boolean created = userDao.registerAdmin(config.getAdminLogin(), hash);
            if (created) {
                System.out.println("[Server] Default admin created: login='"
                        + config.getAdminLogin() + "'");
            } else {
                System.out.println("[Server] Admin '" + config.getAdminLogin()
                        + "' already exists, skipping creation.");
            }
        } catch (SQLException e) {
            System.err.println("[Server] Failed to init default admin: " + e.getMessage());
        }
    }

    /**
     * Регистрирует нового пользователя с ролью USER.
     */
    public AuthResult register(User user) {
        if (user == null || user.getLogin() == null || user.getLogin().isBlank()) {
            return AuthResult.fail("Login cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return AuthResult.fail("Password cannot be empty");
        }
        if (user.getLogin().length() > 255) {
            return AuthResult.fail("Login is too long (max 255 chars)");
        }
        try {
            String hash = PasswordHasher.hash(user.getPassword());
            boolean ok = userDao.register(user.getLogin(), hash);
            if (!ok) return AuthResult.fail("User '" + user.getLogin() + "' already exists");
            return AuthResult.success("Registered as '" + user.getLogin() + "'");
        } catch (SQLException e) {
            return AuthResult.fail("DB error during registration: " + e.getMessage());
        }
    }

    /**
     * Проверяет учётные данные. Вызывается перед каждой командой.
     */
    public AuthResult authenticate(User user) {
        if (user == null || user.getLogin() == null || user.getLogin().isBlank()) {
            return AuthResult.fail("Not authenticated: missing credentials");
        }
        if (user.getPassword() == null) {
            return AuthResult.fail("Not authenticated: missing password");
        }
        try {
            String hash = PasswordHasher.hash(user.getPassword());
            boolean ok = userDao.checkCredentials(user.getLogin(), hash);
            if (!ok) return AuthResult.fail("Invalid login or password");
            return AuthResult.success("OK");
        } catch (SQLException e) {
            return AuthResult.fail("DB error during authentication: " + e.getMessage());
        }
    }

    /**
     * Возвращает роль пользователя: "USER", "ADMIN" или "USER" по умолчанию.
     */
    public String getRole(String login) {
        try {
            String role = userDao.getRole(login);
            return (role != null) ? role : "USER";
        } catch (SQLException e) {
            System.err.println("[Server] Failed to get role for " + login + ": " + e.getMessage());
            return "USER"; // безопасный дефолт
        }
    }

    /**
     * Повышает пользователя до ADMIN. Вызывается только из PromoteUserHandler.
     *
     * @param targetLogin логин для повышения.
     * @return результат.
     */
    public AuthResult promoteToAdmin(String targetLogin) {
        if (targetLogin == null || targetLogin.isBlank()) {
            return AuthResult.fail("Target login cannot be empty");
        }
        try {
            boolean ok = userDao.promoteToAdmin(targetLogin);
            if (!ok) {
                // Проверяем причину
                if (!userDao.exists(targetLogin)) {
                    return AuthResult.fail("User '" + targetLogin + "' does not exist");
                }
                return AuthResult.fail("User '" + targetLogin + "' is already an admin");
            }
            return AuthResult.success("User '" + targetLogin + "' promoted to ADMIN");
        } catch (SQLException e) {
            return AuthResult.fail("DB error: " + e.getMessage());
        }
    }

    /**
     * Понижает ADMIN до USER. Нельзя понизить самого себя.
     *
     * @param targetLogin логин для понижения.
     * @param callerLogin логин того кто выполняет.
     * @return результат.
     */
    public AuthResult demoteToUser(String targetLogin, String callerLogin) {
        if (targetLogin == null || targetLogin.isBlank()) {
            return AuthResult.fail("Target login cannot be empty");
        }
        try {
            UserDao.DemoteResult r = userDao.demoteToUser(targetLogin, callerLogin);
            return r.success ? AuthResult.success(r.message) : AuthResult.fail(r.message);
        } catch (SQLException e) {
            return AuthResult.fail("DB error: " + e.getMessage());
        }
    }

    /** Результат операции аутентификации/регистрации/управления ролями. */
    public static class AuthResult {
        private final boolean ok;
        private final String message;

        private AuthResult(boolean ok, String message) {
            this.ok = ok; this.message = message;
        }

        public static AuthResult success(String msg) { return new AuthResult(true, msg); }
        public static AuthResult fail(String msg)    { return new AuthResult(false, msg); }

        public boolean isOk()      { return ok; }
        public String getMessage() { return message; }
    }
}
