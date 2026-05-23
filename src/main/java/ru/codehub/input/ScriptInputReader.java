package ru.codehub.input;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;

/**
 * Реализация интерфейса UserInputReader для чтения данных из файла скрипта.
 */
public class ScriptInputReader implements UserInputReader, Closeable {
    private final BufferedReader reader;
    private String nextLine;
    private boolean eof;

    public ScriptInputReader(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
        this.eof = false;
        advance();
    }

    private void advance() {
        try {
            nextLine = reader.readLine();
            if (nextLine == null) {
                eof = true;
            }
        } catch (IOException e) {
            eof = true;
        }
    }

    @Override
    public String readLine() {
        if (eof) {
            return null; // Возвращаем null вместо исключения для корректной остановки
        }
        String line = nextLine;
        advance();
        return line;
    }

    @Override
    public String readLine(String prompt) {
        return readLine();
    }

    @Override
    public boolean hasNextLine() {
        return !eof;
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    /**
     * Пропускает текущую строку в файле.
     * Вызывает advance(), чтобы переместить указатель на следующую строку.
     */
    @Override
    public void skip() {
        if (!eof) {
            advance();
        }
    }

    // Добавим проверку текущей строки (пригодится для AddCommand)
    public String peekLine() {
        return nextLine;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}