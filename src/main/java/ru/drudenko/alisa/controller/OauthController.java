package ru.drudenko.alisa.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drudenko.alisa.dto.oauth.YandexToken;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.OauthClient;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.model.Token;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;
import ru.drudenko.alisa.repository.TokenRepository;
import ru.drudenko.alisa.service.google.GmailCredentials;
import ru.drudenko.alisa.service.google.GoogleTokenExtractor;
import ru.drudenko.alisa.service.yandex.YandexTokenExtractor;

import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Transactional
@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {


    private final YandexTokenExtractor yandexTokenExtractor;
    private final GoogleTokenExtractor googleTokenExtractor;
    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final VelocityEngine velocityEngine;

    @Autowired
    public OauthController(YandexTokenExtractor yandexTokenExtractor,
                           GoogleTokenExtractor googleTokenExtractor,
                           OtpRepository otpRepository,
                           ClientRepository clientRepository,
                           TokenRepository tokenRepository,
                           VelocityEngine velocityEngine) {
        this.yandexTokenExtractor = yandexTokenExtractor;
        this.googleTokenExtractor = googleTokenExtractor;
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
        this.velocityEngine = velocityEngine;
    }

    @GetMapping(value = "/yandex", produces = {"text/html;charset=UTF-8"})
    ResponseEntity yandex(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) {

        YandexToken yandexToken = yandexTokenExtractor.getToken(code);

        Otp otp = otpRepository.findByValueAndExpiredAndTokenIsNull(state.trim(), false).orElseThrow(RuntimeException::new);
        Client client = Optional.ofNullable(otp.getClient()).orElseThrow(RuntimeException::new);

        Token token = client.getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(OauthClient.YANDEX))
                .findFirst()
                .orElseGet(() -> {
                    Token t = new Token();
                    t.setAccessToken(yandexToken.getAccess_token());
                    t.setRefreshToken(yandexToken.getRefresh_token());
                    t.setOauthClient(OauthClient.YANDEX);
                    t.setClient(client);
                    return tokenRepository.save(t);
                });

        Otp newOtp = new Otp();
        newOtp.setClient(client);
        newOtp.setToken(token);
        String otpByClient = String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
        newOtp.setValue(otpByClient);
        otpRepository.save(newOtp);

        Template t = velocityEngine.getTemplate("src/main/resources/templates/code.html", "utf-8");
        VelocityContext context = new VelocityContext();
        context.put("otpByClient", otpByClient);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        return ResponseEntity
                .ok()
                .body(writer.toString());
    }

    @GetMapping(value = "/google", produces = {"text/html;charset=UTF-8"})
    ResponseEntity google(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) throws GeneralSecurityException, IOException {
        GmailCredentials credentials = googleTokenExtractor.getToken(code);
        System.out.println(code);
        return ResponseEntity
                .ok().build();
    }
}
