package ru.drudenko.alisa.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

    @Transactional
    void deleteByRefAndType(String clientId, OtpType type);

    Optional<Otp> findByValueAndExpiredAndType(String value, boolean expired, OtpType type);

    List<Otp> findByExpiredAndCreateTimeBeforeAndType(boolean expired, Instant date, OtpType type);
}
