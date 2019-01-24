package ru.drudenko.alisa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.dto.dialog.req.Command;
import ru.drudenko.alisa.dto.dialog.req.Entity;
import ru.drudenko.alisa.model.Client;
import ru.drudenko.alisa.model.Otp;
import ru.drudenko.alisa.repository.ClientRepository;
import ru.drudenko.alisa.repository.OtpRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlisaServiceImpl implements AlisaService {

    private final ClientRepository clientRepository;
    private final OtpRepository otpRepository;

    private static final List<String> TOKENS_STEP1 = Arrays.asList("привяжи", "устройство");
    private static final List<String> TOKENS_STEP3 = Arrays.asList("привяжи", "учетку");

    @Autowired
    public AlisaServiceImpl(ClientRepository clientRepository, OtpRepository otpRepository) {
        this.clientRepository = clientRepository;
        this.otpRepository = otpRepository;
    }

    @Override
    public String getText(Command command) {
        List<String> tokens = command.getRequest().getNlu().getTokens();
        if (tokens.containsAll(TOKENS_STEP1)) {
            return step1(command.getSession().getUserId());
        }
        if (tokens.containsAll(TOKENS_STEP3)) {

            String code = String.join("", command.getRequest().getNlu().getEntities()
                    .stream()
                    .filter(entity -> entity.getType().equals("YANDEX.NUMBER"))
                    .map(Entity::getValue)
                    .map(String::valueOf)
                    .collect(Collectors.toList()));
            return step3(command.getSession().getUserId(), code);
        }
        return "Что то не понятное.";
    }

    private String step1(String clientId) {
        String otpByClient = String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
        Client client = clientRepository.findByClientId(clientId).orElseGet(() -> {
            Client client1 = new Client();
            client1.setClientId(clientId);
            return client1;
        });
        clientRepository.save(client);
        otpRepository.findByClientId(clientId).ifPresent(otpRepository::delete);

        Otp otp = new Otp();
        otp.setClientId(clientId);
        otp.setValue(otpByClient);
        otpRepository.save(otp);

        return "Зайдите на сайт бота, войдите под учетной записью Яндекс, и введите код. " + otpByClient;
    }

    private String step3(String clientId, String otp) {
        Client client = clientRepository.findByClientId(clientId).orElseThrow(RuntimeException::new);

        Optional<Otp> byValueAndIsActive = otpRepository.findByValueAndExpiredAndPersonIdIsNotNull(otp, false);
        if (!byValueAndIsActive.isPresent()) {
            return "Не верный код подтверждения. Или он был выслан больше минуты назад.";
        }
        client.setActive(true);
        client.setPersonId(byValueAndIsActive.get().getPersonId());
        clientRepository.save(client);
        byValueAndIsActive.get().setExpired(true);
        otpRepository.save(byValueAndIsActive.get());
        return "Вы успешно привязали учетную запись";
    }
}
