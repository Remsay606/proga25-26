package ru.codehub.server.collection;

import ru.codehub.common.model.MusicBand;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс управления коллекцией.
 *
 * <p>Модифицирующие операции учитывают роль пользователя:
 * <ul>
 *   <li>USER — только свои объекты;</li>
 *   <li>ADMIN — любые объекты (параметры isAdmin / effectiveOwner=null).</li>
 * </ul>
 * </p>
 */
public interface CollectionManager {

    // ---- Модифицирующие операции ----

    MusicBand add(MusicBand band, String owner) throws Exception;

    /**
     * Обновляет объект.
     *
     * @param id            ID объекта.
     * @param newBand       новые данные.
     * @param callerLogin   логин выполняющего (для лога).
     * @param ownerFilter   если null — ADMIN bypass (любой объект); иначе — только этого владельца.
     */
    ModifyResult update(long id, MusicBand newBand,
                        String callerLogin, String ownerFilter) throws Exception;

    /**
     * Удаляет объект по ID.
     *
     * @param id      ID.
     * @param owner   логин пользователя.
     * @param isAdmin если true — удаляет без проверки владельца.
     */
    ModifyResult remove(long id, String owner, boolean isAdmin) throws Exception;

    /** Удаляет все объекты текущего пользователя. */
    int clearOwned(String owner) throws Exception;

    /** [ADMIN] Удаляет все объекты коллекции. */
    int clearAll() throws Exception;

    /**
     * Удаляет первый элемент.
     *
     * @param owner   логин пользователя.
     * @param isAdmin если true — ADMIN bypass.
     */
    ModifyResult removeHead(String owner, boolean isAdmin) throws Exception;

    ModifyResult addIfMin(MusicBand band, String owner) throws Exception;

    // ---- Операции чтения ----

    List<MusicBand> getAll();
    Optional<MusicBand> getById(long id);
    Optional<MusicBand> getMinByCreationDate();
    Map<String, Long> groupByName();
    List<MusicBand> filterByNameContains(String substring);
    int size();
    Date getInitializationDate();
    String getCollectionType();
    Collection<MusicBand> getCollection();
    void loadFromDatabase() throws Exception;

    /** Результат модифицирующей операции. */
    class ModifyResult {
        public final boolean success;
        public final String message;
        public final MusicBand band;

        private ModifyResult(boolean success, String message, MusicBand band) {
            this.success = success; this.message = message; this.band = band;
        }
        public static ModifyResult ok(String msg, MusicBand b) { return new ModifyResult(true, msg, b); }
        public static ModifyResult ok(String msg)              { return new ModifyResult(true, msg, null); }
        public static ModifyResult fail(String msg)            { return new ModifyResult(false, msg, null); }
    }
}
