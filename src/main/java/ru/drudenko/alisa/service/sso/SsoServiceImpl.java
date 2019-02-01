package ru.drudenko.alisa.service.sso;

import org.springframework.stereotype.Service;
import ru.drudenko.alisa.model.AlisaClient;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.model.OtpType;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;
import ru.drudenko.alisa.repository.TokenRepository;
import ru.drudenko.alisa.utils.RandomUtils;

import java.util.Optional;

@Service
public class SsoServiceImpl implements SsoService {
    private final ClientRepository clientRepository;
    private final OtpRepository otpRepository;
    private final TokenRepository tokenRepository;

    public SsoServiceImpl(ClientRepository clientRepository,
                          OtpRepository otpRepository,
                          TokenRepository tokenRepository) {
        this.clientRepository = clientRepository;
        this.otpRepository = otpRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String step1(String clientId) {
        String otpByClient = RandomUtils.getOtp();
        AlisaClient alisaClient = clientRepository.findById(clientId).orElseGet(() -> {
            AlisaClient alisaClient1 = new AlisaClient();
            alisaClient1.setId(clientId);
            return alisaClient1;
        });
        clientRepository.save(alisaClient);
        otpRepository.deleteByRefAndType(clientId, OtpType.ALISA_STATION);

        Otp otp = new Otp();
        otp.setRef(alisaClient.getId());
        otp.setType(OtpType.ALISA_STATION);
        otp.setValue(otpByClient);
        otpRepository.save(otp);

        return "Зайдите на сайт бота, войдите под учетной записью Яндекс, и введите код - " + otpByClient;
    }

    @Override
    public String step2(String clientId) {
        return null;
    }

    @Override
    public String step3(String clientId, String otp) {
        AlisaClient alisaClient = clientRepository.findById(clientId).orElseThrow(RuntimeException::new);

        Optional<Otp> byValueAndIsActive = otpRepository.findByValueAndExpiredAndType(otp, false, OtpType.TOKEN);
        if (!byValueAndIsActive.isPresent()) {
            return "Не верный код подтверждения. Или он был выслан больше минуты назад.";
        }
        alisaClient.setActive(true);
        alisaClient.getTokens().add(tokenRepository.getOne(byValueAndIsActive.get().getRef()));
        clientRepository.save(alisaClient);
        byValueAndIsActive.get().setExpired(true);
        otpRepository.save(byValueAndIsActive.get());
        return "Вы успешно привязали учетную запись";
    }
}
