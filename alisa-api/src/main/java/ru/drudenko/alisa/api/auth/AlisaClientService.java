package ru.drudenko.alisa.api.auth;

public interface AlisaClientService {

    TokenDto getTokenByUserIdAndOauthClient(String userId, String oauthClient);
}
