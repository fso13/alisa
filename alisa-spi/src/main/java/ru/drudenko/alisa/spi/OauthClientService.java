package ru.drudenko.alisa.spi;


import ru.drudenko.alisa.api.auth.TokenResponseDto;

public interface OauthClientService {
    TokenResponseDto getToken(String code) throws Exception;

    OauthClient getOauthClient();
}
