package ru.drudenko.alisa.service.yandex;

import ru.drudenko.alisa.dto.oauth.YandexToken;

public interface YandexTokenExtractor {
    YandexToken getToken(String code);
}
