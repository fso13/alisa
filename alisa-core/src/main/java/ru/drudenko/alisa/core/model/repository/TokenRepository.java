package ru.drudenko.alisa.core.model.repository;

import ru.drudenko.alisa.core.model.Token;

import javax.transaction.Transactional;

@Transactional
public interface TokenRepository extends Repository<Token> {
    Token save(Token token);
}
