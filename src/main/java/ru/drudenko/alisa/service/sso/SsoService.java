package ru.drudenko.alisa.service.sso;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SsoService {

    String step1(String clientId);

    String step2(String clientId);

    String step3(String clientId, String otp);
}
