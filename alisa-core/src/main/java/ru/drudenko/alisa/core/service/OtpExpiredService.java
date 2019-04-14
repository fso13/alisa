package ru.drudenko.alisa.core.service;

import javax.transaction.Transactional;

@Transactional
public interface OtpExpiredService {

    void expired();
}
