package ru.drudenko.alisa.service.alisa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.dto.dialog.req.Command;
import ru.drudenko.alisa.dto.dialog.req.Entity;
import ru.drudenko.alisa.dto.passport.Passport;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.OauthClient;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlisaServiceImpl implements AlisaService {

    private final ClientRepository clientRepository;
    private final OtpRepository otpRepository;
    private final RestTemplate restTemplate;

    private static final List<String> TOKENS_STEP1 = Arrays.asList("привяжи", "устройство");
    private static final List<String> TOKENS_STEP3 = Arrays.asList("привяжи", "учетку");
    private static final List<String> WHAT_IS_MY_NAME1 = Arrays.asList("как", "меня", "зовут");
    private static final List<String> WHAT_IS_MY_NAME2 = Arrays.asList("скажи", "мое", "имя");

    @Autowired
    public AlisaServiceImpl(ClientRepository clientRepository, OtpRepository otpRepository, final RestTemplate restTemplate) {
        this.clientRepository = clientRepository;
        this.otpRepository = otpRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getText(Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        if (tokens.containsAll(TOKENS_STEP1)) {
            return step1(command.getSession().getUserId());
        }
        if (tokens.containsAll(TOKENS_STEP3)) {

            String code = String.join("", command.getRequest().getNlu().getEntities()
                    .stream()
                    .filter(entity -> entity.getType().equals("YANDEX.NUMBER"))
                    .map(Entity::getValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
            return step3(command.getSession().getUserId(), code);
        }

        if (tokens.containsAll(WHAT_IS_MY_NAME1) || tokens.containsAll(WHAT_IS_MY_NAME2)) {
            return whatIsMyName(command.getSession().getUserId());
        }
        return "Что то не понятное.";
    }

    private String whatIsMyName(String clientId) {
        Client client = clientRepository.findByClientId(clientId).orElseThrow(RuntimeException::new);


        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "OAuth " + client.getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(OauthClient.YANDEX))
                .findFirst().get().getAccessToken());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Passport> responseEntity = restTemplate.
                exchange("https://login.yandex.ru/info?format=json&with_openid_identity=true",
                        HttpMethod.GET,
                        request,
                        ParameterizedTypeReference.forType(Passport.class));

        return responseEntity.getBody().getRealName();
    }

    private String step1(String clientId) {
        String otpByClient = String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
        Client client = clientRepository.findByClientId(clientId).orElseGet(() -> {
            Client client1 = new Client();
            client1.setClientId(clientId);
            return client1;
        });
        clientRepository.save(client);
        otpRepository.deleteByClientId(clientId);

        Otp otp = new Otp();
        otp.setClient(client);
        otp.setValue(otpByClient);
        otpRepository.save(otp);

        return "Зайдите на сайт бота, войдите под учетной записью Яндекс, и введите код - " + otpByClient;
    }

    private String step3(String clientId, String otp) {
        Client client = clientRepository.findByClientId(clientId).orElseThrow(RuntimeException::new);

        Optional<Otp> byValueAndIsActive = otpRepository.findByValueAndExpiredAndTokenIsNotNull(otp, false);
        if (!byValueAndIsActive.isPresent()) {
            return "Не верный код подтверждения. Или он был выслан больше минуты назад.";
        }
        client.setActive(true);
        client.getTokens().add(byValueAndIsActive.get().getToken());
        clientRepository.save(client);
        byValueAndIsActive.get().setExpired(true);
        otpRepository.save(byValueAndIsActive.get());
        return "Вы успешно привязали учетную запись";
    }
}
