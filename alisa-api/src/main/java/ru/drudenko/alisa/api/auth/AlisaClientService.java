package ru.drudenko.alisa.api.auth;

public interface AlisaClientService {

    TokenResponseDto getTokenByUserIdAndOauthClient(TokenRequestDto tokenRequestDto);
}
