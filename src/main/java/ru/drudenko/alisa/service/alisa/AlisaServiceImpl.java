package ru.drudenko.alisa.service.alisa;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.drudenko.alisa.dto.dialog.req.Entity;
import ru.drudenko.alisa.dto.passport.Passport;
import ru.drudenko.alisa.model.AlisaClient;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.service.sso.SsoService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlisaServiceImpl implements AlisaService {

    private final SsoService ssoService;
    private final ClientRepository clientRepository;

    private static final List<String> TOKENS_STEP1 = Arrays.asList("привяжи", "устройство");
    private static final List<String> TOKENS_STEP3 = Arrays.asList("привяжи", "учетку");
    private static final List<String> WHAT_IS_MY_NAME1 = Arrays.asList("как", "меня", "зовут");
    private static final List<String> WHAT_IS_MY_NAME2 = Arrays.asList("скажи", "мое", "имя");

    @Autowired
    public AlisaServiceImpl(SsoService ssoService,
                            ClientRepository clientRepository) {
        this.ssoService = ssoService;
        this.clientRepository = clientRepository;
    }

    @Override
    public String getText(Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        if (tokens.containsAll(TOKENS_STEP1)) {
            return ssoService.step1(command.getSession().getUserId());
        }
        if (tokens.containsAll(TOKENS_STEP3)) {

            String code = String.join("", command.getRequest().getNlu().getEntities()
                    .stream()
                    .filter(entity -> entity.getType().equals("YANDEX.NUMBER"))
                    .map(Entity::getValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
            return ssoService.step3(command.getSession().getUserId(), code);
        }

        if (tokens.containsAll(WHAT_IS_MY_NAME1) || tokens.containsAll(WHAT_IS_MY_NAME2)) {
            return whatIsMyName(command.getSession().getUserId());
        }
        return "Что то не понятное.";
    }

    private String whatIsMyName(String clientId) {
        AlisaClient alisaClient = clientRepository.findById(clientId).orElseThrow(RuntimeException::new);

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
    private final RestTemplate restTemplate = restTemplate();

    private static RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }
}
