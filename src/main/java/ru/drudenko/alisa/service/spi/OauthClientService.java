package ru.drudenko.alisa.service.spi;

import ru.drudenko.alisa.model.OauthClient;

public interface OauthClientService {
    TokenDto getToken(String code) throws Exception;
    OauthClient getOauthClient();
}
