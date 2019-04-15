package ru.drudenko.alisa.core.service.impl;

import ru.drudenko.alisa.api.dialog.dto.req.Command;
import ru.drudenko.alisa.api.dialog.dto.req.Entity;
import ru.drudenko.alisa.core.model.AlisaClient;
import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;
import ru.drudenko.alisa.core.model.repository.AlisaClientRepository;
import ru.drudenko.alisa.core.model.repository.OtpRepository;
import ru.drudenko.alisa.core.model.repository.TokenRepository;
import ru.drudenko.alisa.core.service.sso.SsoCommandService;
import ru.drudenko.alisa.core.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SsoCommandServiceImpl implements SsoCommandService {
    private static final List<String> TOKENS_STEP1 = Arrays.asList("привяжи", "устройство");
    private static final List<String> TOKENS_STEP3_1 = Arrays.asList("активируй", "учетку");
    private static final List<String> TOKENS_STEP3_2 = Arrays.asList("активируй", "учётку");

    private final AlisaClientRepository alisaClientRepository;
    private final OtpRepository otpRepository;
    private final TokenRepository tokenRepository;

    public SsoCommandServiceImpl(AlisaClientRepository alisaClientRepository,
                                 OtpRepository otpRepository,
                                 TokenRepository tokenRepository) {
        this.alisaClientRepository = alisaClientRepository;
        this.otpRepository = otpRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String step1(String clientId) {
        String otpByClient = RandomUtils.getOtp();
        AlisaClient alisaClient = alisaClientRepository.findById(clientId).orElseGet(() -> {
            AlisaClient alisaClient1 = new AlisaClient();
            alisaClient1.setId(clientId);
            return alisaClient1;
        });
        alisaClientRepository.save(alisaClient);
        otpRepository.deleteByRefAndType(clientId, OtpType.ALISA_STATION);

        Otp otp = new Otp();
        otp.setRef(alisaClient.getId());
        otp.setType(OtpType.ALISA_STATION);
        otp.setValue(otpByClient);
        otpRepository.save(otp);

        return "Зайдите на сайт alisa-java.herokuapp.com, и введите код - " + otpByClient;
    }

    @Override
    public String step3(String clientId, String otp) {
        AlisaClient alisaClient = alisaClientRepository.findById(clientId).orElseThrow(RuntimeException::new);

        Optional<Otp> byValueAndIsActive = otpRepository.findByValueAndExpiredAndType(otp, false, OtpType.TOKEN);
        if (!byValueAndIsActive.isPresent()) {
            return "Не верный код подтверждения. Или он был выслан больше минуты назад.";
        }
        alisaClient.setActive(true);
        alisaClient.getTokens().add(tokenRepository.findById(byValueAndIsActive.get().getRef()).get());
        alisaClientRepository.save(alisaClient);
        byValueAndIsActive.get().setExpired(true);
        otpRepository.save(byValueAndIsActive.get());
        return "Вы успешно привязали учетную запись";
    }

    @Override
    public boolean doFilter(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        return tokens.containsAll(TOKENS_STEP1) || tokens.containsAll(TOKENS_STEP3_1) || tokens.containsAll(TOKENS_STEP3_2);
    }

    @Override
    public String getMessage(final Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        if (tokens.containsAll(TOKENS_STEP1)) {
            return step1(command.getSession().getUserId());
        }
        if (tokens.containsAll(TOKENS_STEP3_1) || tokens.containsAll(TOKENS_STEP3_2)) {
            String code = command.getRequest().getNlu().getEntities()
                    .stream()
                    .filter(entity -> entity.getType().equals("YANDEX.NUMBER"))
                    .map(Entity::getValue)
                    .map(String::valueOf)
                    .collect(Collectors.joining(""));
            return step3(command.getSession().getUserId(), code);
        }

        return null;
    }

    @Override
    public String getCommands() {
        return "привяжи устройство \n активируй учетку";
    }
}
