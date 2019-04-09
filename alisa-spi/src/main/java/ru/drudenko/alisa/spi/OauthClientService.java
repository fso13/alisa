package ru.drudenko.alisa.spi;


import ru.drudenko.alisa.api.auth.TokenDto;

public interface OauthClientService {
    TokenDto getToken(String code) throws Exception;

    OauthClient getOauthClient();
}
