package ru.drudenko.alisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drudenko.alisa.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
}
