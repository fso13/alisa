package ru.drudenko.alisa.core.model.repository;

import java.util.Optional;

public interface Repository<T> {
    Optional<T> findById(Object id);
}
