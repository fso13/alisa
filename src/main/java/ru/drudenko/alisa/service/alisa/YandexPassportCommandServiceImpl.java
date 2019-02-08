package ru.drudenko.alisa.service.alisa;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.dto.dialog.req.Command;
import ru.drudenko.alisa.dto.passport.Passport;
import ru.drudenko.alisa.model.AlisaClient;
import ru.drudenko.alisa.repository.AlisaClientRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class YandexPassportCommandServiceImpl implements CommandService {
    private static final List<String> WHAT_IS_MY_NAME1 = Arrays.asList("как", "меня", "зовут");
    private static final List<String> WHAT_IS_MY_NAME2 = Arrays.asList("скажи", "мое", "имя");
    private static final List<String> WHAT_IS_MY_NAME3 = Arrays.asList("скажи", "моё", "имя");
    private final AlisaClientRepository alisaClientRepository;
    private final RestTemplate restTemplate = restTemplate();

    public YandexPassportCommandServiceImpl(final AlisaClientRepository alisaClientRepository) {
        this.alisaClientRepository = alisaClientRepository;
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
        AlisaClient alisaClient = alisaClientRepository.findById(clientId).orElseThrow(RuntimeException::new);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "OAuth " + alisaClient.getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals("yandex"))
                .findFirst().get().getAccessToken());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Passport> responseEntity = restTemplate.
                exchange("https://login.yandex.ru/info?format=json&with_openid_identity=true",
                        HttpMethod.GET,
                        request,
                        ParameterizedTypeReference.forType(Passport.class));

        return responseEntity.getBody().getRealName();
    }

    @Override
    public String getCommands() {
        return "как меня зовут \n скажи мое имя";
    }
}
