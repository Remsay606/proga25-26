package ru.codehub.server.db;

import ru.codehub.common.model.Coordinates;
import ru.codehub.common.model.MusicBand;
import ru.codehub.common.model.MusicGenre;
import ru.codehub.common.model.Studio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO для работы с таблицей {@code music_bands}.
 * <p>
 * Инкапсулирует все SQL-операции над объектами коллекции. ID генерируется
 * средствами БД ({@code BIGSERIAL} / sequence) и возвращается через
 * {@code RETURNING id}.
 * </p>
 *
 * <p>Паттерн: <b>DAO</b>. Все методы используют {@link PreparedStatement}
 * для защиты от SQL-инъекций.</p>
 */
public class MusicBandDao {

    private final DatabaseManager db;

    /**
     * @param db менеджер подключения к БД.
     */
    public MusicBandDao(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Вставляет новый объект, ID присваивается базой данных через sequence.
     *
     * @param band  объект для вставки (его поле id игнорируется).
     * @param owner логин владельца.
     * @return ID, сгенерированный базой данных.
     * @throws SQLException при ошибке БД.
     */
    public long insert(MusicBand band, String owner) throws SQLException {
        String sql = "INSERT INTO music_bands " +
                "(name, coord_x, coord_y, creation_date, participants, genre, studio_name, studio_address, owner_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            fillBandParams(ps, band, owner);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new SQLException("INSERT did not return generated id");
            }
        }
    }

    /**
     * Обновляет существующий объект <b>только если</b> он принадлежит указанному владельцу.
     *
     * @param id    идентификатор объекта.
     * @param band  новые данные.
     * @param owner логин пользователя, выполняющего обновление.
     * @return {@code true}, если строка обновлена (объект существует и принадлежит владельцу).
     * @throws SQLException при ошибке БД.
     */
    public boolean update(long id, MusicBand band, String owner) throws SQLException {
        String sql = "UPDATE music_bands SET " +
                "name = ?, coord_x = ?, coord_y = ?, creation_date = ?, participants = ?, " +
                "genre = ?, studio_name = ?, studio_address = ? " +
                "WHERE id = ? AND owner_login = ?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, band.getName());
            ps.setLong(2, band.getCoordinates().getX());
            ps.setDouble(3, band.getCoordinates().getY());
            ps.setTimestamp(4, new Timestamp(band.getCreationDate().getTime()));
            ps.setLong(5, band.getNumberOfParticipants());
            ps.setString(6, band.getGenre().name());
            ps.setString(7, band.getStudio() != null ? band.getStudio().getName() : null);
            ps.setString(8, band.getStudio() != null ? band.getStudio().getAddress() : null);
            ps.setLong(9, id);
            ps.setString(10, owner);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет объект по ID <b>только если</b> он принадлежит указанному владельцу.
     *
     * @param id    идентификатор.
     * @param owner логин пользователя.
     * @return {@code true}, если строка удалена.
     * @throws SQLException при ошибке БД.
     */
    public boolean deleteByIdAndOwner(long id, String owner) throws SQLException {
        String sql = "DELETE FROM music_bands WHERE id = ? AND owner_login = ?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, owner);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет все объекты, принадлежащие указанному владельцу.
     *
     * @param owner логин пользователя.
     * @return количество удалённых строк.
     * @throws SQLException при ошибке БД.
     */
    public int deleteAllByOwner(String owner) throws SQLException {
        String sql = "DELETE FROM music_bands WHERE owner_login = ?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner);
            return ps.executeUpdate();
        }
    }

    /**
     * Проверяет, существует ли объект и кто его владелец.
     *
     * @param id идентификатор.
     * @return логин владельца или {@code null}, если объект не найден.
     * @throws SQLException при ошибке БД.
     */
    public String findOwner(long id) throws SQLException {
        String sql = "SELECT owner_login FROM music_bands WHERE id = ?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("owner_login");
                return null;
            }
        }
    }

    /**
     * Загружает все объекты из БД (для наполнения коллекции в памяти при старте).
     *
     * @return список всех объектов.
     * @throws SQLException при ошибке БД.
     */
    public List<MusicBand> loadAll() throws SQLException {
        String sql = "SELECT * FROM music_bands";
        List<MusicBand> result = new ArrayList<>();
        Connection conn = db.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    /**
     * Заполняет параметры PreparedStatement для INSERT.
     */
    private void fillBandParams(PreparedStatement ps, MusicBand band, String owner) throws SQLException {
        ps.setString(1, band.getName());
        ps.setLong(2, band.getCoordinates().getX());
        ps.setDouble(3, band.getCoordinates().getY());
        Date created = band.getCreationDate() != null ? band.getCreationDate() : new Date();
        ps.setTimestamp(4, new Timestamp(created.getTime()));
        ps.setLong(5, band.getNumberOfParticipants());
        ps.setString(6, band.getGenre().name());
        ps.setString(7, band.getStudio() != null ? band.getStudio().getName() : null);
        ps.setString(8, band.getStudio() != null ? band.getStudio().getAddress() : null);
        ps.setString(9, owner);
    }

    /**
     * Преобразует строку ResultSet в объект {@link MusicBand}.
     */
    private MusicBand mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        long cx = rs.getLong("coord_x");
        double cy = rs.getDouble("coord_y");
        Timestamp ts = rs.getTimestamp("creation_date");
        long participants = rs.getLong("participants");
        String genreStr = rs.getString("genre");
        String studioName = rs.getString("studio_name");
        String studioAddr = rs.getString("studio_address");
        String owner = rs.getString("owner_login");

        Coordinates coords = new Coordinates(cx, cy);
        Studio studio = new Studio(studioName, studioAddr);
        MusicGenre genre = MusicGenre.valueOf(genreStr);
        Date created = ts != null ? new Date(ts.getTime()) : new Date();

        return new MusicBand(id, name, coords, created, participants, genre, studio, owner);
    }

    // ---- Дополнительные методы для ADMIN ----

    /**
     * Удаляет объект по ID без проверки владельца (ADMIN bypass).
     */
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM music_bands WHERE id = ?";
        Connection conn = db.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет все объекты коллекции (ADMIN clear).
     *
     * @return количество удалённых строк.
     */
    public int deleteAll() throws SQLException {
        String sql = "DELETE FROM music_bands";
        Connection conn = db.getConnection();
        try (Statement st = conn.createStatement()) {
            return st.executeUpdate(sql);
        }
    }
}
