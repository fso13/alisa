package ru.drudenko.alisa.core.model.repository.impl;

import ru.drudenko.alisa.core.model.AlisaClient;
import ru.drudenko.alisa.core.model.repository.AlisaClientRepository;

import javax.persistence.EntityManager;

public class AlisaClientRepositoryImpl extends AbstractRepository<AlisaClient> implements AlisaClientRepository {

    @Override
    public AlisaClient save(final AlisaClient alisaClient) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(alisaClient); // cascades the tool & skill relationships
        entityManager.getTransaction().commit();
        return alisaClient;
    }
}
