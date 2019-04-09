package ru.drudenko.alisa.core.service.sso;

import org.springframework.transaction.annotation.Transactional;
import ru.drudenko.alisa.api.dialog.CommandService;

@Transactional
public interface SsoCommandService extends CommandService {

    String step1(String clientId);

    String step2(String clientId);

    String step3(String clientId, String otp);
}
