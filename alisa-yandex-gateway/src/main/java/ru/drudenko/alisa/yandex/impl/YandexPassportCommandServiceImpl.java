package ru.drudenko.alisa.yandex.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.api.auth.AlisaClientService;
import ru.drudenko.alisa.api.auth.TokenRequestDto;
import ru.drudenko.alisa.api.auth.TokenResponseDto;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.yandex.Passport;

import java.util.Arrays;
import java.util.List;

public class YandexPassportCommandServiceImpl implements CommandService {
    private static final List<String> WHAT_IS_MY_NAME1 = Arrays.asList("как", "меня", "зовут");
    private static final List<String> WHAT_IS_MY_NAME2 = Arrays.asList("скажи", "мое", "имя");
    private static final List<String> WHAT_IS_MY_NAME3 = Arrays.asList("скажи", "моё", "имя");
    private final AlisaClientService alisaClientService;
    private final RestTemplate restTemplate = restTemplate();

    public YandexPassportCommandServiceImpl(final AlisaClientService alisaClientService) {
        this.alisaClientService = alisaClientService;
    }

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }

    @Override
    public boolean doFilter(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        return tokens.containsAll(WHAT_IS_MY_NAME1) || tokens.containsAll(WHAT_IS_MY_NAME2) || tokens.containsAll(WHAT_IS_MY_NAME3);
    }

    @Override
    public String getMessage(final Command command) {
        return whatIsMyName(command.getSession().getUserId());
    }

    private String whatIsMyName(String clientId) {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder().oauthClient(clientId).userId("yandex").build();
        TokenResponseDto token = alisaClientService.getTokenByUserIdAndOauthClient(tokenRequestDto);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "OAuth " + token.getAccessToken());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Passport> responseEntity = restTemplate.
                exchange("https://login.yandex.ru/info?format=json&with_openid_identity=true",
                        HttpMethod.GET,
                        request,
                        ParameterizedTypeReference.forType(Passport.class));

        return responseEntity.getBody() != null ? responseEntity.getBody().getRealName() : null;
    }

    @Override
    public String getCommands() {
        return "как меня зовут \n скажи мое имя";
    }
}
