package ru.drudenko.alisa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class YandexRestTemplateConfiguration {

    @Bean
    public RestTemplate yandexRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }
}
