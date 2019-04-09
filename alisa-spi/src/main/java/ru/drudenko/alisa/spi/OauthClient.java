package ru.drudenko.alisa.spi;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class OauthClient {
    private String name;
}
