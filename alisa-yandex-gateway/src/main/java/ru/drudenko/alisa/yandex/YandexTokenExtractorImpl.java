package ru.drudenko.alisa.yandex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.spi.OauthClient;
import ru.drudenko.alisa.spi.OauthClientService;

@Service
public class YandexTokenExtractorImpl implements OauthClientService {
    private final RestTemplate restTemplate = restTemplate();

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }

    @Value("${app.yandex.client_id}")
    private String clientId;

    @Value("${app.yandex.client_secret}")
    private String clientSecret;

    @Override
    public YandexTokenDto getToken(final String code) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", Long.valueOf(code));
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<YandexTokenDto> responseEntity = restTemplate.
                exchange("https://oauth.yandex.ru/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(YandexTokenDto.class));

        return responseEntity.getBody();
    }

    @Override
    public OauthClient getOauthClient() {
        return OauthClient.builder().name("yandex").build();

    }
}
