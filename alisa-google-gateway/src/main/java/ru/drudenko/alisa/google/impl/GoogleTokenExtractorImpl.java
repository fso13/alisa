package ru.drudenko.alisa.google.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.google.GmailCredentialsResponseDto;
import ru.drudenko.alisa.google.configuration.GoogleSettings;
import ru.drudenko.alisa.spi.OauthClient;
import ru.drudenko.alisa.spi.OauthClientService;

public final class GoogleTokenExtractorImpl implements OauthClientService {
    private final RestTemplate restTemplate = restTemplate();

    private final GoogleSettings googleSettings;

    public GoogleTokenExtractorImpl(final GoogleSettings googleSettings) {
        this.googleSettings = googleSettings;
    }

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }

    @Override
    public GmailCredentialsResponseDto getToken(final String code) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", googleSettings.getRedirect_url());
        map.add("client_secret", googleSettings.getClient_secret());
        map.add("client_id", googleSettings.getClient_id());
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        return (GmailCredentialsResponseDto) restTemplate.
                exchange("https://www.googleapis.com/oauth2/v4/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(GmailCredentialsResponseDto.class)).getBody();

    }

    @Override
    public OauthClient getOauthClient() {
        return OauthClient.builder().name("google").build();
    }
}
