package ru.codehub.command.impl;

import ru.codehub.command.Command;
import ru.codehub.command.CommandRegistry;
import ru.codehub.input.ScriptInputReader;

import java.io.File;
import java.util.Set;

/**
 * Команда для считывания и исполнения скрипта из указанного файла.
 * Скрипт содержит команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 */
public class ExecuteScriptCommand implements Command {
    private final CommandRegistry registry;
    private final ScriptExecutor executor;
    private final Set<String> executingScripts;

    /**
     * Конструктор команды.
     * @param registry реестр доступных команд.
     * @param executor интерфейс для логики выполнения команд скрипта.
     * @param executingScripts множество путей к текущим исполняемым скриптам для предотвращения рекурсии.
     */
    public ExecuteScriptCommand(CommandRegistry registry, ScriptExecutor executor, Set<String> executingScripts) {
        this.registry = registry;
        this.executor = executor;
        this.executingScripts = executingScripts;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "execute the script from the specified file";
    }

    /**
     * Выполняет скрипт по указанному пути.
     * Проверяет существование файла и предотвращает зацикливание (рекурсивный вызов одного и того же скрипта).
     * @param args массив аргументов, где первый элемент — путь к файлу скрипта.
     */
    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: execute_script <file_path>");
            return;
        }

        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Script file not found: " + filePath);
            return;
        }

        String absolutePath = file.getAbsolutePath();

        // Проверка на рекурсию
        if (executingScripts.contains(absolutePath)) {
            System.out.println("Error: Recursive script execution detected for: " + filePath);
            return;
        }

        executingScripts.add(absolutePath);
        try (ScriptInputReader scriptReader = new ScriptInputReader(filePath)) {
            executor.executeScript(scriptReader, registry);
            System.out.println("Script executed successfully: " + filePath);
        } catch (Exception e) {
            System.out.println("Error executing script: " + e.getMessage());
        } finally {
            executingScripts.remove(absolutePath);
        }
    }

    /**
     * Внутренний интерфейс для выполнения логики скрипта,
     * который должен быть реализован в основном классе приложения или обработчике.
     */
    public interface ScriptExecutor {
        void executeScript(ScriptInputReader reader, CommandRegistry registry);
    }
}