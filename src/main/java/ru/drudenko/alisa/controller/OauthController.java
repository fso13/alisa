package ru.drudenko.alisa.controller;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;
import ru.drudenko.alisa.service.yandex.YandexTokenExtractor;

import java.io.IOException;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {


    private final YandexTokenExtractor yandexTokenExtractor;
    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;
    private final VelocityEngine velocityEngine;

    @Autowired
    public OauthController(YandexTokenExtractor yandexTokenExtractor, OtpRepository otpRepository, ClientRepository clientRepository, VelocityEngine velocityEngine) {
        this.yandexTokenExtractor = yandexTokenExtractor;
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
        this.velocityEngine = velocityEngine;
    }

    @GetMapping(value = "/yandex", produces = {"text/html;charset=UTF-8"})
    ResponseEntity yandex(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) {

        String token = yandexTokenExtractor.getToken(code);

        Otp otp = otpRepository.findByValueAndExpiredAndPersonIdIsNull(state.trim(), false).orElseThrow(RuntimeException::new);
        Client client = clientRepository.findByClientId(otp.getClientId()).orElseThrow(RuntimeException::new);
        client.setPersonId(token);

        Otp newOtp = new Otp();
        newOtp.setClientId(otp.getClientId());
        newOtp.setPersonId(token);
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
    ResponseEntity google(@RequestParam(name = "state") String state, @RequestParam(name = "code") String code) {

        System.out.println(code);
        return ResponseEntity
                .ok().build();
    }




}
