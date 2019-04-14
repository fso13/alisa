package ru.drudenko.alisa.core.model.repository.impl;

import ru.drudenko.alisa.core.model.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public abstract class AbstractRepository<T> implements Repository<T> {
    private static EntityManagerFactory entityManagerFactory = null;

    public Optional<T> findById(Object id) {
        return (Optional<T>) Optional.ofNullable(openEntityManager().find(getClazz(), id));

    }

    private Class getClazz() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    static EntityManager openEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("alisa");
        }
        return entityManagerFactory.createEntityManager();
    }
}
