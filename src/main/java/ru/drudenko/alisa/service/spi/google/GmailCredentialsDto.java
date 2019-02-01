package ru.drudenko.alisa.service.spi.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import ru.drudenko.alisa.service.spi.TokenDto;

@Getter
@Setter
@JsonIgnoreProperties
public class GmailCredentialsDto extends TokenDto {
    private String userEmail;
    private String clientId;
    private String clientSecret;

    GmailCredentialsDto(String accessToken, String refreshToken) {
        super(accessToken, refreshToken);
    }
}
