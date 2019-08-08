package ru.drudenko.alisa.core.service;

import ru.drudenko.alisa.api.auth.TokenResponseDto;
import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.spi.OauthClient;

import javax.transaction.Transactional;

@Transactional
public interface AlisaService {
    String getText(Command command);

    String generationOtp(String state, OauthClient client, TokenResponseDto tokenResponseDto);
}
