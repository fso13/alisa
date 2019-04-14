package ru.drudenko.alisa.core.service.impl;

import ru.drudenko.alisa.core.model.OtpType;
import ru.drudenko.alisa.core.model.repository.OtpRepository;
import ru.drudenko.alisa.core.service.OtpExpiredService;

import java.time.Instant;

public class OtpExpiredServiceImpl implements OtpExpiredService {

    private final OtpRepository otpRepository;

    public OtpExpiredServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public void expired() {
        otpRepository.findByExpiredAndCreateTimeBeforeAndType(false, Instant.now().minusSeconds(60), OtpType.TOKEN)
                .forEach(otp -> {
                    otp.setExpired(true);
                    otpRepository.save(otp);
                });

    }
}
