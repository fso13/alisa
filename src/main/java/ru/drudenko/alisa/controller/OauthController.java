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
import ru.drudenko.alisa.model.AlisaClient;
import ru.drudenko.alisa.model.OauthClient;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.model.OtpType;
import ru.drudenko.alisa.model.Token;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;
import ru.drudenko.alisa.repository.TokenRepository;
import ru.drudenko.alisa.service.spi.OauthClientService;
import ru.drudenko.alisa.service.spi.TokenDto;
import ru.drudenko.alisa.utils.RandomUtils;

import java.io.StringWriter;
import java.util.List;

@Transactional
@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {

    private final List<OauthClientService> oauthClientServices;
    private final OtpRepository otpRepository;
    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final VelocityEngine velocityEngine;

    @Autowired
    public OauthController(List<OauthClientService> oauthClientServices,
                           OtpRepository otpRepository,
                           ClientRepository clientRepository,
                           TokenRepository tokenRepository,
                           VelocityEngine velocityEngine) {
        this.oauthClientServices = oauthClientServices;
        this.otpRepository = otpRepository;
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
        this.velocityEngine = velocityEngine;
    }

    @Transactional
    @GetMapping(produces = {"text/html;charset=UTF-8"})
    ResponseEntity oauth(@RequestParam(name = "client_id") String oauthClient,
                         @RequestParam(name = "state") String state,
                         @RequestParam(name = "code") String code) throws Exception {
        OauthClient client = OauthClient.builder().name(oauthClient).build();

        TokenDto tokenDto = oauthClientServices.stream()
                .filter(oauthClientService -> oauthClientService.getOauthClient().equals(client))
                .findFirst()
                .get()
                .getToken(code);
        Otp otp = otpRepository.findByValueAndExpiredAndType(state.trim(), false, OtpType.ALISA_STATION).orElseThrow(RuntimeException::new);
        AlisaClient alisaClient = clientRepository.findById(otp.getRef()).orElseThrow(RuntimeException::new);

        Token token = alisaClient.getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(client.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Token t = new Token();
                    t.setAccessToken(tokenDto.getAccessToken());
                    t.setRefreshToken(tokenDto.getRefreshToken());
                    t.setOauthClient(client.getName());
                    t.setAlisaClient(alisaClient);
                    return tokenRepository.save(t);
                });

        String otpByClient = RandomUtils.getOtp();
        Otp newOtp = new Otp();
        newOtp.setRef(token.getId());
        newOtp.setType(OtpType.TOKEN);
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
}
