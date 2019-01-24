package ru.drudenko.alisa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.drudenko.alisa.dto.oauth.Token;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {

    @Value("${app.client_id}")
    private String client_id;

    @Value("${app.client_secret}")
    private String client_secret;

    private final RestTemplate yandexRestTemplate;
    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public OauthController(RestTemplate yandexRestTemplate,
                           OtpRepository otpRepository,
                           ClientRepository clientRepository) {

        this.yandexRestTemplate = yandexRestTemplate;
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
    }

    @GetMapping(value = "/yandex")
    void yandex(HttpServletResponse response, @RequestParam(name = "state") String state, @RequestParam(name = "code") String code) throws IOException {

        String token = getToken(code);

        Otp otp = otpRepository.findByValueAndExpiredAndPersonIdIsNull(state.trim(), false).orElseThrow(RuntimeException::new);
        Client client = clientRepository.findByClientId(otp.getClientId()).orElseThrow(RuntimeException::new);
        client.setPersonId(token);

        Otp newOtp = new Otp();
        newOtp.setClientId(otp.getClientId());
        newOtp.setPersonId(token);
        String otpByClient = String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
        newOtp.setValue(otpByClient);
        otpRepository.save(newOtp);
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().println("Скажите Алисе код :" + otpByClient + ", в течении минуты.");
    }

    private String getToken(String code) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", Long.valueOf(code));
        map.add("client_id", client_id);
        map.add("client_secret", client_secret);

        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(client_id, client_secret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Token> responseEntity = yandexRestTemplate.
                exchange("https://oauth.yandex.ru/token",
                        HttpMethod.POST,
                        request,
                        ParameterizedTypeReference.forType(Token.class));

        return responseEntity.getBody().getAccess_token();
    }

}
