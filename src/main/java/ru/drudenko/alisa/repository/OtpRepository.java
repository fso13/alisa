package ru.drudenko.alisa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.drudenko.alisa.model.Otp;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

    Optional<Otp> findByClientId(String clientId);

    Optional<Otp> findByValueAndExpiredAndPersonIdIsNotNull(String value, boolean expired);

    Optional<Otp> findByValueAndExpiredAndPersonIdIsNull(String value, boolean expired);

    List<Otp> findByExpiredAndCreateTimeBeforeAndPersonIdIsNotNull(boolean expired, Instant date);
}
