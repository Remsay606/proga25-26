package ru.codehub.input;

import java.util.Scanner;

/**
 * Класс, отвечающий за чтение ввода пользователя из стандартного потока ввода (консоли).
 * Реализует интерфейс UserInputReader.
 */
public class ConsoleInputReader implements UserInputReader {
    private final Scanner scanner;

    /**
     * Конструктор инициализирует сканер для чтения из System.in.
     */
    public ConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Читает следующую строку, введенную пользователем.
     * * @return Введенная пользователем строка.
     */
    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Выводит приглашение к вводу и читает введенную строку.
     * * @param prompt Текст сообщения, который увидит пользователь перед вводом.
     * @return Введенная пользователем строка.
     */
    @Override
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Проверяет, есть ли в потоке ввода еще данные для чтения.
     * * @return true, если есть следующая строка, иначе false.
     */
    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Указывает, является ли данный тип ввода интерактивным (через консоль).
     * * @return Всегда true для данного класса.
     */
    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public void skip() {

    }
}