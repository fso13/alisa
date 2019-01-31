package ru.drudenko.alisa.service.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleTokenExtractorImpl implements GoogleTokenExtractor {
    private final RestTemplate restTemplate;

    @Value("${app.google.client_id}")
    private String client_id;

    @Value("${app.google.client_secret}")
    private String client_secret;

    @Value("${app.google.redirect_url}")
    private String redirectUrl;

    @Autowired
    public GoogleTokenExtractorImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GmailCredentials getToken(final String code) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", redirectUrl);
        map.add("client_secret", client_secret);
        map.add("client_id", client_id);
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        return (GmailCredentials) restTemplate.
                exchange("https://www.googleapis.com/oauth2/v4/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(GmailCredentials.class)).getBody();

    }


}
