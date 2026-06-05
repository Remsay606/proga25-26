package ru.codehub.server.command.impl;

import ru.codehub.common.network.CommandRequest;
import ru.codehub.common.network.CommandResponse;
import ru.codehub.server.collection.CollectionManager;
import ru.codehub.server.command.ServerCommandHandler;
import ru.codehub.server.io.CollectionWriter;

import java.io.IOException;

/**
 * Обработчик команды {@code save} — доступна ТОЛЬКО на сервере.
 * <p>
 * Клиент не может отправить эту команду: команда {@code save} удалена
 * из клиентского модуля согласно требованиям задания.
 * Сохранение автоматически выполняется при завершении работы сервера.
 * Эта команда позволяет сохранить коллекцию принудительно через консоль сервера.
 * </p>
 */
public class SaveHandler implements ServerCommandHandler {

    private final CollectionManager manager;
    private final CollectionWriter writer;
    private final String filePath;

    /**
     * @param manager  менеджер коллекции.
     * @param writer   объект для записи в файл.
     * @param filePath путь к файлу сохранения.
     */
    public SaveHandler(CollectionManager manager, CollectionWriter writer, String filePath) {
        this.manager = manager;
        this.writer = writer;
        this.filePath = filePath;
    }

    @Override
    public String getCommandName() { return "save"; }

    /**
     * Сохраняет текущее состояние коллекции в CSV-файл.
     *
     * @param request запрос (аргументы не используются).
     * @return ответ с подтверждением или ошибкой записи.
     */
    @Override
    public CommandResponse handle(CommandRequest request) {
        try {
            writer.write(manager.getCollection(), filePath);
            return CommandResponse.ok("Collection saved to " + filePath);
        } catch (IOException e) {
            return CommandResponse.error("Save failed: " + e.getMessage());
        }
    }
}
