package ru.drudenko.alisa.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.drudenko.alisa.model.OtpType;
import ru.drudenko.alisa.repository.OtpRepository;

import java.time.Instant;

@Service
public class OtpExpiredServiceImpl implements OtpExpiredService {

    private final OtpRepository otpRepository;

    public OtpExpiredServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    @Scheduled(cron = "*/5 * * * * *")
    public void expired() {
        otpRepository.findByExpiredAndCreateTimeBeforeAndType(false, Instant.now().minusSeconds(60), OtpType.TOKEN)
                .forEach(otp -> {
                    otp.setExpired(true);
                    otpRepository.save(otp);
                });

    }
}
