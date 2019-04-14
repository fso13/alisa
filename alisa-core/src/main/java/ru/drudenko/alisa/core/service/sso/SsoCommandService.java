package ru.drudenko.alisa.core.service.sso;

import ru.drudenko.alisa.api.dialog.CommandService;

import javax.transaction.Transactional;

@Transactional
public interface SsoCommandService extends CommandService {

    String step1(String clientId);

    String step3(String clientId, String otp);
}
