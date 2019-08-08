package ru.drudenko.alisa.api.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenRequestDto {
    private final String userId;
    private final String oauthClient;
}
