package ru.codehub.common.network;

import java.io.*;

/**
 * Утилитный класс для сериализации и десериализации объектов в массив байт.
 * <p>
 * Используется для упаковки {@link CommandRequest} и {@link CommandResponse}
 * в тело UDP-датаграмм при обмене данными между клиентом и сервером.
 * </p>
 *
 * <p>Паттерн: <b>Facade</b> — скрывает детали работы с {@link ObjectOutputStream}
 * и {@link ObjectInputStream}.</p>
 */
public final class Serializer {

    /** Утилитный класс — конструктор закрыт. */
    private Serializer() {}

    /**
     * Сериализует объект в массив байт.
     *
     * @param obj объект, реализующий {@link Serializable}.
     * @return массив байт с сериализованным объектом.
     * @throws IOException если возникла ошибка ввода-вывода.
     */
    public static byte[] serialize(Serializable obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует объект из массива байт.
     *
     * @param data массив байт.
     * @return восстановленный объект.
     * @throws IOException            если возникла ошибка ввода-вывода.
     * @throws ClassNotFoundException если класс объекта не найден в classpath.
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
}
