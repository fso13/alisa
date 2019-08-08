package ru.drudenko.alisa.google.configuration;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public final class GoogleSettings {
    private final String client_id;
    private final String client_secret;
    private final String redirect_url;

    public GoogleSettings(@Value("${app.google.client_id}") String client_id,
                          @Value("${app.google.client_secret}") String client_secret,
                          @Value("${app.google.redirect_url}") String redirect_url) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.redirect_url = redirect_url;
    }
}
