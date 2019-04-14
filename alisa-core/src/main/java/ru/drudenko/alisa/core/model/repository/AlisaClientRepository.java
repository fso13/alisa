package ru.drudenko.alisa.core.model.repository;

import ru.drudenko.alisa.core.model.AlisaClient;

public interface AlisaClientRepository extends Repository<AlisaClient> {

    AlisaClient save(AlisaClient alisaClient);
}

