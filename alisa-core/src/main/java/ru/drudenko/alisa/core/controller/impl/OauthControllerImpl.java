package ru.drudenko.alisa.core.controller.impl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import ru.drudenko.alisa.api.auth.TokenResponseDto;
import ru.drudenko.alisa.core.controller.OauthController;
import ru.drudenko.alisa.core.service.AlisaService;
import ru.drudenko.alisa.spi.OauthClient;
import ru.drudenko.alisa.spi.OauthClientService;

import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.util.List;

public class OauthControllerImpl implements OauthController {

    private final List<OauthClientService> oauthClientServices;
    private final AlisaService alisaService;
    private final VelocityEngine velocityEngine;

    private OauthControllerImpl(final List<OauthClientService> oauthClientServices,
                                final AlisaService alisaService,
                                final VelocityEngine velocityEngine) {
        this.oauthClientServices = oauthClientServices;
        this.alisaService = alisaService;
        this.velocityEngine = velocityEngine;
    }

    @Override
    public Response oauth(String agentId, String state, String code) throws Exception {
        OauthClient client = OauthClient.builder().name(agentId).build();

        TokenResponseDto tokenResponseDto = oauthClientServices.stream()
                .filter(oauthClientService -> oauthClientService.getOauthClient().equals(client))
                .findFirst()
                .get()
                .getToken(code);

        String otpByClient = alisaService.generationOtp(state, client, tokenResponseDto);


        Template t = velocityEngine.getTemplate("src/main/resources/templates/code.html", "utf-8");
        VelocityContext context = new VelocityContext();
        context.put("otpByClient", otpByClient);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        return Response.ok(writer.toString()).build();
    }
}
