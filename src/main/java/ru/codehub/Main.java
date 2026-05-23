package ru.codehub;

import ru.codehub.collection.CollectionManager;
import ru.codehub.collection.MusicBandCollectionManager;
import ru.codehub.command.Command;
import ru.codehub.command.CommandRegistry;
import ru.codehub.command.impl.*;
import ru.codehub.input.ConsoleInputReader;
import ru.codehub.input.ScriptInputReader;
import ru.codehub.input.UserInputReader;
import ru.codehub.io.CollectionReader;
import ru.codehub.io.CollectionWriter;
import ru.codehub.io.CsvCollectionReader;
import ru.codehub.io.CsvCollectionWriter;
import ru.codehub.model.MusicBand;
import ru.codehub.util.CollectionValidator;
import ru.codehub.util.IdGenerator;
import ru.codehub.util.MusicBandFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Точка входа в приложение.
 * Отвечает за инициализацию всех компонентов, загрузку коллекции из файла
 * и запуск цикла обработки пользовательских команд.
 */
public class Main {
    private final CollectionManager collectionManager;
    private final CommandRegistry commandRegistry;
    private final CollectionWriter writer;
    private final UserInputReader consoleReader;
    private final MusicBandFactory bandFactory;
    private final String filePath;
    /** Множество для отслеживания запущенных скриптов и предотвращения рекурсии */
    private final Set<String> executingScripts = new HashSet<>();
    /**
     * Конструктор основного класса. Инициализирует зависимости и создает реестр команд.
     * @param filePath путь к CSV-файлу с данными коллекции.
     */
    public Main(String filePath) {
        this.filePath = filePath;
        this.consoleReader = new ConsoleInputReader();
        this.writer = new CsvCollectionWriter();

        IdGenerator idGenerator = new IdGenerator();
        this.bandFactory = new MusicBandFactory(idGenerator);
        this.collectionManager = new MusicBandCollectionManager(idGenerator);

        loadCollection();
        this.commandRegistry = createRegistry(consoleReader);
    }
    /**
     * Загружает элементы из файла, указанного при старте программы.
     */
    private void loadCollection() {
        CollectionReader reader = new CsvCollectionReader();
        CollectionValidator validator = new CollectionValidator();
        try {
            Collection<MusicBand> bands = reader.read(filePath);
            List<MusicBand> validBands = validator.validate(bands);
            if (validBands.size() < bands.size()) {
                System.out.println("Warning: " + (bands.size() - validBands.size()) + " invalid elements were skipped.");
            }
            collectionManager.loadCollection(validBands);
            System.out.println("Loaded " + validBands.size() + " elements from " + filePath);
        } catch (Exception e) {
            System.out.println("Warning: Could not load collection from " + filePath + ": " + e.getMessage());
            System.out.println("Starting with empty collection.");
        }
    }
    /**
     * Создает и наполняет реестр доступных команд.
     * Здесь регистрируются все функциональные возможности приложения.
     * @param inputReader источник ввода данных для команд.
     * @return полностью настроенный объект {@link CommandRegistry}.
     */
    private CommandRegistry createRegistry(UserInputReader inputReader) {
        CommandRegistry registry = new CommandRegistry();
        registry.register(new HelpCommand(registry));
        registry.register(new InfoCommand(collectionManager));
        registry.register(new ShowCommand(collectionManager));
        registry.register(new AddCommand(collectionManager, inputReader, bandFactory));
        registry.register(new UpdateCommand(collectionManager, inputReader, bandFactory));
        registry.register(new RemoveByIdCommand(collectionManager));
        registry.register(new ClearCommand(collectionManager));
        registry.register(new SaveCommand(collectionManager, writer, filePath));
        registry.register(new ExecuteScriptCommand(registry, this::executeScript, executingScripts));
        registry.register(new ExitCommand());
        registry.register(new RemoveHeadCommand(collectionManager));
        registry.register(new AddIfMaxCommand(collectionManager, inputReader, bandFactory));
        registry.register(new HistoryCommand(registry));
        registry.register(new MinByCreationDateCommand(collectionManager));
        registry.register(new GroupCountingByNameCommand(collectionManager));
        registry.register(new FilterContainsNameCommand(collectionManager));
        return registry;
    }
    /**
     * Основной метод запуска приложения. Загружает данные из файла и входит в интерактивный цикл.
     */
    public void run() {
        System.out.println("Music Band Collection Manager");
        System.out.println("Type 'help' to see available commands.");
        System.out.println();

        while (consoleReader.hasNextLine()) {
            System.out.print("> ");
            String line = consoleReader.readLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (processCommand(line, commandRegistry)) {
                break;
            }
        }
    }

    /**
     * Разбирает введенную строку на имя команды и аргументы, затем выполняет её.
     * @param line полная строка ввода.
     * @param registry реестр команд для поиска.
     * @return true, если команда завершает программу.
     */
    private boolean processCommand(String line, CommandRegistry registry) {
        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        Optional<Command> command = registry.getCommand(commandName);
        if (command.isPresent()) {
            registry.addToHistory(commandName);
            command.get().execute(args);
            return command.get().isTerminating();
        } else {
            System.out.println("Unknown command: " + commandName);
            System.out.println("Type 'help' to see available commands.");
            return false;
        }
    }
    /**
     * Логика выполнения внешнего скрипта.
     * Создает временный реестр команд, использующий ScriptInputReader.
     */
    private void executeScript(ScriptInputReader scriptReader, CommandRegistry parentRegistry) {
        CommandRegistry scriptRegistry = createRegistry(scriptReader);

        while (scriptReader.hasNextLine()) {
            String line = scriptReader.readLine().trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (processCommand(line, scriptRegistry)) {
                break;
            }
        }
    }
    /**
     * Точка старта JVM.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar lab5.jar <csv_file_path>");
            System.err.println("Please provide the path to the CSV file as a command-line argument.");
            System.exit(1);
        }

        String filePath = args[0];
        Main app = new Main(filePath);
        app.run();
    }
}
