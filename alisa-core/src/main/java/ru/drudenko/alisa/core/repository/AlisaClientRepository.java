package ru.drudenko.alisa.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drudenko.alisa.core.model.AlisaClient;

@Repository
public interface AlisaClientRepository extends JpaRepository<AlisaClient, String> {
}

