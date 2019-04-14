package ru.drudenko.alisa.core.model.repository.impl;

import ru.drudenko.alisa.core.model.Token;
import ru.drudenko.alisa.core.model.repository.TokenRepository;

import javax.persistence.EntityManager;

public class TokenRepositoryImpl extends AbstractRepository<Token> implements TokenRepository {
    @Override
    public Token save(final Token token) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(token); // cascades the tool & skill relationships
        entityManager.getTransaction().commit();
        return token;
    }
}