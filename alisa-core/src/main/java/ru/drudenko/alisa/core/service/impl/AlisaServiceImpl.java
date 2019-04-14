package ru.drudenko.alisa.core.service.impl;

import ru.drudenko.alisa.api.auth.AlisaClientService;
import ru.drudenko.alisa.api.auth.TokenDto;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.core.model.AlisaClient;
import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;
import ru.drudenko.alisa.core.model.Token;
import ru.drudenko.alisa.core.model.repository.AlisaClientRepository;
import ru.drudenko.alisa.core.model.repository.OtpRepository;
import ru.drudenko.alisa.core.model.repository.TokenRepository;
import ru.drudenko.alisa.core.service.AlisaService;
import ru.drudenko.alisa.core.utils.RandomUtils;
import ru.drudenko.alisa.spi.OauthClient;

import java.util.List;

public class AlisaServiceImpl implements AlisaService, AlisaClientService {
    private final AlisaClientRepository alisaClientRepository;
    private final OtpRepository otpRepository;
    private final TokenRepository tokenRepository;
    private final List<CommandService> commandServices;

    private AlisaServiceImpl(final AlisaClientRepository alisaClientRepository,
                             final OtpRepository otpRepository,
                             final TokenRepository tokenRepository,
                             final List<CommandService> commandServices) {
        this.alisaClientRepository = alisaClientRepository;
        this.otpRepository = otpRepository;
        this.tokenRepository = tokenRepository;
        this.commandServices = commandServices;
    }

    @Override
    public String getText(Command command) {
        return commandServices.stream().filter(service -> service.doFilter(command)).findFirst().orElse(new CommandService() {
            @Override
            public boolean doFilter(final Command command) {
                return false;
            }

            @Override
            public String getMessage(final Command command) {
                return "Что то не понятное.";
            }

            @Override
            public String getCommands() {
                return null;
            }
        }).getMessage(command);
    }

    @Override
    public String generationOtp(String state, OauthClient client, TokenDto tokenDto) {

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

        return otpByClient;
    }

    @Override
    public TokenDto getTokenByUserIdAndOauthClient(final String userId, final String oauthClient) {
        return alisaClientRepository.findById(userId).get()
                .getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(oauthClient))
                .findFirst()
                .map(token -> new TokenDto(token.getAccessToken(), token.getRefreshToken())).orElse(null);
    }
}
