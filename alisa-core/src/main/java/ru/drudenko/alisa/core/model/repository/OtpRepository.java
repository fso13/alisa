package ru.drudenko.alisa.core.model.repository;

import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Transactional
public interface OtpRepository extends Repository<Otp>{

    void deleteByRefAndType(String clientId, OtpType type);

    Optional<Otp> findByValueAndExpiredAndType(String value, boolean expired, OtpType type);

    List<Otp> findByExpiredAndCreateTimeBeforeAndType(boolean expired, Instant date, OtpType type);

    Otp save(Otp newOtp);
}
