package ru.drudenko.alisa.dto.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YandexToken {

    private String token_type;
    private String access_token;
    private Long expires_in;
    private String refresh_token;
}
