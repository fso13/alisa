package ru.drudenko.alisa.core.service.alisa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.api.auth.AlisaClientService;
import ru.drudenko.alisa.api.auth.TokenDto;
import ru.drudenko.alisa.api.dialog.CommandService;
import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.core.repository.AlisaClientRepository;

import java.util.List;

@Service
public class AlisaServiceImpl implements AlisaService, AlisaClientService {
    private final AlisaClientRepository alisaClientRepository;
    private final List<CommandService> commandServices;

    @Autowired
    public AlisaServiceImpl(final AlisaClientRepository alisaClientRepository,
                            final List<CommandService> commandServices) {
        this.alisaClientRepository = alisaClientRepository;
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
    public TokenDto getTokenByUserIdAndOauthClient(final String userId, final String oauthClient) {
        return alisaClientRepository.getOne(userId)
                .getTokens()
                .stream()
                .filter(token1 -> token1.getOauthClient().equals(oauthClient))
                .findFirst()
                .map(token -> new TokenDto(token.getAccessToken(), token.getRefreshToken())).orElse(null);
    }
}
