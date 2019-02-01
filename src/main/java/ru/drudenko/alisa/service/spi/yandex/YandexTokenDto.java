package ru.drudenko.alisa.service.spi.yandex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.drudenko.alisa.service.spi.TokenDto;

@Getter
@Setter
@JsonIgnoreProperties
public class YandexTokenDto extends TokenDto {

    @JsonProperty(value = "tokenType")
    private String tokenType;
    @JsonProperty(value = "accessToken")
    private String accessToken;
    @JsonProperty(value = "expires_in")
    private Long expiresIn;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
