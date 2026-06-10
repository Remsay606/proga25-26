package ru.codehub.server.collection;

import ru.codehub.common.model.MusicBand;
import ru.codehub.server.db.MusicBandDao;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Потокобезопасная реализация {@link CollectionManager} поверх PostgreSQL и PriorityQueue.
 *
 * <p>Коллекция в памяти обновляется только после успешной записи в БД.</p>
 * <p>Синхронизация через объект-монитор {@link #lock}.</p>
 * <p>ADMIN-операции игнорируют проверку владельца.</p>
 */
public class MusicBandCollectionManager implements CollectionManager {

    private final PriorityQueue<MusicBand> collection = new PriorityQueue<>();
    private final Object lock = new Object();
    private final MusicBandDao dao;
    private final Date initializationDate = new Date();

    public MusicBandCollectionManager(MusicBandDao dao) { this.dao = dao; }

    @Override
    public MusicBand add(MusicBand band, String owner) throws Exception {
        long id = dao.insert(band, owner);
        band.setId(id);
        band.setOwner(owner);
        synchronized (lock) { collection.add(band); }
        return band;
    }

    @Override
    public ModifyResult update(long id, MusicBand newBand,
                               String callerLogin, String ownerFilter) throws Exception {
        // ownerFilter == null → ADMIN bypass (не проверяем владельца)
        String currentOwner = dao.findOwner(id);
        if (currentOwner == null) {
            return ModifyResult.fail("Object with id=" + id + " does not exist");
        }
        if (ownerFilter != null && !currentOwner.equals(ownerFilter)) {
            return ModifyResult.fail("Access denied: object id=" + id + " belongs to '"
                    + currentOwner + "'");
        }

        Date originalDate;
        synchronized (lock) {
            originalDate = collection.stream()
                    .filter(b -> b.getId() == id)
                    .map(MusicBand::getCreationDate)
                    .findFirst().orElse(new Date());
        }
        newBand.setId(id);
        newBand.setOwner(currentOwner); // владелец не меняется при обновлении
        newBand.setCreationDate(originalDate);

        // При ADMIN-обновлении передаём реального владельца объекта в DAO
        boolean dbOk = dao.update(id, newBand, currentOwner);
        if (!dbOk) return ModifyResult.fail("Update failed in database");

        synchronized (lock) {
            collection.removeIf(b -> b.getId() == id);
            collection.add(newBand);
        }
        return ModifyResult.ok("Object id=" + id + " updated by " + callerLogin, newBand);
    }

    @Override
    public ModifyResult remove(long id, String owner, boolean isAdmin) throws Exception {
        String currentOwner = dao.findOwner(id);
        if (currentOwner == null) {
            return ModifyResult.fail("Object with id=" + id + " does not exist");
        }
        if (!isAdmin && !currentOwner.equals(owner)) {
            return ModifyResult.fail("Access denied: object id=" + id
                    + " belongs to '" + currentOwner + "'");
        }
        // ADMIN удаляет без проверки владельца
        boolean dbOk = isAdmin
                ? dao.deleteById(id)
                : dao.deleteByIdAndOwner(id, owner);
        if (!dbOk) return ModifyResult.fail("Remove failed in database");

        synchronized (lock) { collection.removeIf(b -> b.getId() == id); }
        return ModifyResult.ok("Object id=" + id + " removed");
    }

    @Override
    public int clearOwned(String owner) throws Exception {
        int deleted = dao.deleteAllByOwner(owner);
        synchronized (lock) { collection.removeIf(b -> owner.equals(b.getOwner())); }
        return deleted;
    }

    @Override
    public int clearAll() throws Exception {
        int deleted = dao.deleteAll();
        synchronized (lock) { collection.clear(); }
        return deleted;
    }

    @Override
    public ModifyResult removeHead(String owner, boolean isAdmin) throws Exception {
        MusicBand head;
        synchronized (lock) { head = collection.peek(); }
        if (head == null) return ModifyResult.fail("Collection is empty");

        if (!isAdmin && !owner.equals(head.getOwner())) {
            return ModifyResult.fail("Access denied: head object belongs to '"
                    + head.getOwner() + "'");
        }
        boolean dbOk = isAdmin
                ? dao.deleteById(head.getId())
                : dao.deleteByIdAndOwner(head.getId(), owner);
        if (!dbOk) return ModifyResult.fail("Remove head failed in database");

        synchronized (lock) { collection.remove(head); }
        return ModifyResult.ok("Head object removed", head);
    }

    @Override
    public ModifyResult addIfMin(MusicBand band, String owner) throws Exception {
        boolean isMin;
        synchronized (lock) {
            MusicBand min = collection.peek();
            isMin = (min == null) ||
                    (band.getNumberOfParticipants() < min.getNumberOfParticipants());
        }
        if (!isMin) return ModifyResult.fail("Not added: size is not smaller than minimum");
        MusicBand added = add(band, owner);
        return ModifyResult.ok("Added (id=" + added.getId() + ")", added);
    }

    // ---- Чтение: только память ----

    @Override
    public List<MusicBand> getAll() {
        synchronized (lock) {
            return collection.stream().sorted().collect(Collectors.toList());
        }
    }

    @Override
    public Optional<MusicBand> getById(long id) {
        synchronized (lock) {
            return collection.stream().filter(b -> b.getId() == id).findFirst();
        }
    }

    @Override
    public Optional<MusicBand> getMinByCreationDate() {
        synchronized (lock) {
            return collection.stream().min(Comparator.comparing(MusicBand::getCreationDate));
        }
    }

    @Override
    public Map<String, Long> groupByName() {
        synchronized (lock) {
            return collection.stream()
                    .collect(Collectors.groupingBy(MusicBand::getName, Collectors.counting()));
        }
    }

    @Override
    public List<MusicBand> filterByNameContains(String sub) {
        synchronized (lock) {
            return collection.stream()
                    .filter(b -> b.getName().contains(sub))
                    .sorted().collect(Collectors.toList());
        }
    }

    @Override public int size() { synchronized (lock) { return collection.size(); } }
    @Override public Date getInitializationDate() { return initializationDate; }
    @Override public String getCollectionType() { return PriorityQueue.class.getSimpleName(); }
    @Override public Collection<MusicBand> getCollection() {
        synchronized (lock) { return new ArrayList<>(collection); }
    }

    @Override
    public void loadFromDatabase() throws Exception {
        List<MusicBand> all = dao.loadAll();
        synchronized (lock) { collection.clear(); collection.addAll(all); }
    }
}
