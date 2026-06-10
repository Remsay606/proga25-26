package ru.codehub.common.network;

import java.io.Serializable;

/**
 * Объект-ответ, отправляемый сервером клиенту по UDP.
 * <p>
 * Содержит текстовый результат выполнения команды и флаг успешности операции.
 * Передаётся в сериализованном виде внутри UDP-датаграммы.
 * </p>
 */
public class CommandResponse implements Serializable {

    /** Текстовое сообщение с результатом выполнения команды. */
    private final String message;

    /** Флаг: {@code true} — команда выполнена успешно, {@code false} — ошибка. */
    private final boolean success;

    /**
     * Создаёт ответ с заданным сообщением и статусом.
     *
     * @param message текст результата.
     * @param success признак успешного выполнения.
     */
    public CommandResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * Фабричный метод для создания успешного ответа.
     *
     * @param message текст результата.
     * @return ответ со статусом success=true.
     */
    public static CommandResponse ok(String message) {
        return new CommandResponse(message, true);
    }

    /**
     * Фабричный метод для создания ответа об ошибке.
     *
     * @param message описание ошибки.
     * @return ответ со статусом success=false.
     */
    public static CommandResponse error(String message) {
        return new CommandResponse(message, false);
    }

    /** @return текстовое сообщение ответа. */
    public String getMessage() { return message; }

    /** @return {@code true}, если команда выполнена успешно. */
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return (success ? "[OK] " : "[ERROR] ") + message;
    }
}
