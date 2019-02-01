package ru.drudenko.alisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drudenko.alisa.model.AlisaClient;

@Repository
public interface ClientRepository extends JpaRepository<AlisaClient, String> {
}

