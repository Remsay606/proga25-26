package ru.codehub.util;

import ru.codehub.model.MusicBand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionValidator {

    /**
     * Проверяет список групп на валидность и отсутствие дубликатов ID.
     * @param bands исходная коллекция для проверки.
     * @return список только валидных групп.
     */
    public List<MusicBand> validate(Collection<MusicBand> bands) {
        List<MusicBand> valid = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        for (MusicBand band : bands) {
            List<String> errors = validateBand(band);

            if (seenIds.contains(band.getId())) {
                errors.add("duplicate id " + band.getId());
            }

            if (errors.isEmpty()) {
                seenIds.add(band.getId());
                valid.add(band);
            } else {
                System.err.println("Skipping invalid band (id=" + band.getId() + "): " + String.join("; ", errors));
            }
        }

        return valid;
    }

    /**
     * Проверяет конкретный объект группы на соответствие бизнес-правилам.
     * @param band объект для проверки.
     * @return список найденных ошибок (пустой, если объект корректен).
     */
    private List<String> validateBand(MusicBand band) {
        List<String> errors = new ArrayList<>();

        if (band.getId() <= 0) {
            errors.add("id must be > 0");
        }
        if (band.getCreationDate() == null) {
            errors.add("creationDate cannot be null");
        }

        return errors;
    }
}