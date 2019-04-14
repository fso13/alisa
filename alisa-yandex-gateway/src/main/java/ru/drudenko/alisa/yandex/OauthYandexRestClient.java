package ru.drudenko.alisa.yandex;

import javax.ws.rs.POST;

public interface OauthYandexRestClient {

    @POST
    YandexTokenDto getToken();
}
