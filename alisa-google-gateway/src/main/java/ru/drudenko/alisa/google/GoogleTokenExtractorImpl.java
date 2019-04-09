package ru.drudenko.alisa.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.spi.OauthClient;
import ru.drudenko.alisa.spi.OauthClientService;

@Service
public class GoogleTokenExtractorImpl implements OauthClientService {
    private final RestTemplate restTemplate = restTemplate();

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }

    @Value("${app.google.client_id}")
    private String clientId;

    @Value("${app.google.client_secret}")
    private String clientSecret;

    @Value("${app.google.redirect_url}")
    private String redirectUrl;

    @Override
    public GmailCredentialsDto getToken(final String code) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", redirectUrl);
        map.add("client_secret", clientSecret);
        map.add("client_id", clientId);
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        return (GmailCredentialsDto) restTemplate.
                exchange("https://www.googleapis.com/oauth2/v4/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(GmailCredentialsDto.class)).getBody();

    }

    @Override
    public OauthClient getOauthClient() {
        return OauthClient.builder().name("google").build();
    }
}
