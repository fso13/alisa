package ru.drudenko.alisa.core.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drudenko.alisa.core.model.AlisaClient;
import ru.drudenko.alisa.spi.OauthClient;
import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;
import ru.drudenko.alisa.core.model.Token;
import ru.drudenko.alisa.core.repository.AlisaClientRepository;
import ru.drudenko.alisa.core.repository.OtpRepository;
import ru.drudenko.alisa.core.repository.TokenRepository;
import ru.drudenko.alisa.core.utils.RandomUtils;
import ru.drudenko.alisa.spi.OauthClientService;
import ru.drudenko.alisa.api.auth.TokenDto;

import java.io.StringWriter;
import java.util.List;

@Transactional
@RestController
@RequestMapping(value = "/oauth", produces = MediaType.ALL_VALUE)
public class OauthController {

    private final List<OauthClientService> oauthClientServices;
    private final OtpRepository otpRepository;
    private final AlisaClientRepository alisaClientRepository;
    private final TokenRepository tokenRepository;
    private final VelocityEngine velocityEngine;

    @Autowired
    public OauthController(List<OauthClientService> oauthClientServices,
                           OtpRepository otpRepository,
                           AlisaClientRepository alisaClientRepository,
                           TokenRepository tokenRepository,
                           VelocityEngine velocityEngine) {
        this.oauthClientServices = oauthClientServices;
        this.otpRepository = otpRepository;
        this.alisaClientRepository = alisaClientRepository;
        this.tokenRepository = tokenRepository;
        this.velocityEngine = velocityEngine;
    }

    @Transactional
    @GetMapping(value = "/{agentId}", produces = {"text/html;charset=UTF-8"})
    ResponseEntity oauth(@PathVariable("agentId") String agentId,
                         @RequestParam(name = "state") String state,
                         @RequestParam(name = "code") String code) throws Exception {
        OauthClient client = OauthClient.builder().name(agentId).build();

        TokenDto tokenDto = oauthClientServices.stream()
                .filter(oauthClientService -> oauthClientService.getOauthClient().equals(client))
                .findFirst()
                .get()
                .getToken(code);
        Otp otp = otpRepository.findByValueAndExpiredAndType(state.trim(), false, OtpType.ALISA_STATION).orElseThrow(RuntimeException::new);
        AlisaClient alisaClient = alisaClientRepository.findById(otp.getRef()).orElseThrow(RuntimeException::new);

        Token token = alisaClient.getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(client.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Token t = new Token();
                    t.setOauthClient(client.getName());
                    t.setAlisaClient(alisaClient);
                    return t;
                });

        token.setAccessToken(tokenDto.getAccessToken());
        token.setRefreshToken(tokenDto.getRefreshToken());
        token = tokenRepository.save(token);
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
