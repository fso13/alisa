package ru.drudenko.alisa.yandex.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.drudenko.alisa.api.auth.AlisaClientService;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.spi.OauthClientService;
import ru.drudenko.alisa.yandex.impl.YandexPassportCommandServiceImpl;
import ru.drudenko.alisa.yandex.impl.YandexTokenExtractorImpl;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CommandService yandexPassportCommandService(@Autowired AlisaClientService alisaClientService) {
        return new YandexPassportCommandServiceImpl(alisaClientService);
    }

    @Bean
    public OauthClientService yandexTokenExtractor(@Value("${app.yandex.client_id}") String clientId, @Value("${app.yandex.client_secret}") String clientSecret) {
        return new YandexTokenExtractorImpl(clientId, clientSecret);
    }
}
