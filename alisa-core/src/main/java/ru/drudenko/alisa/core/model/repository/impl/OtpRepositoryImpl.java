package ru.drudenko.alisa.core.model.repository.impl;

import ru.drudenko.alisa.core.model.Otp;
import ru.drudenko.alisa.core.model.OtpType;
import ru.drudenko.alisa.core.model.repository.OtpRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OtpRepositoryImpl extends AbstractRepository<Otp> implements OtpRepository {
    @Override
    public void deleteByRefAndType(final String clientId, final OtpType type) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("DELETE FROM Otp A WHERE A.type = :type AND A.ref = :clientId");
        query.setParameter("clientId", clientId);
        query.setParameter("type", type);
        query.getSingleResult();
    }

    @Override
    public Optional<Otp> findByValueAndExpiredAndType(final String value, final boolean expired, final OtpType type) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT A FROM Otp A WHERE A.value = :value AND A.expired = :expired AND A.type = :type");
        query.setParameter("value", value);
        query.setParameter("type", type);
        query.setParameter("expired", expired);
        return Optional.ofNullable((Otp) query.getSingleResult());
    }

    @Override
    public List<Otp> findByExpiredAndCreateTimeBeforeAndType(final boolean expired, final Instant date, final OtpType type) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT A FROM Otp A WHERE A.createTime = :createTime AND A.expired = :expired AND A.type = :type");
        query.setParameter("createTime", date);
        query.setParameter("type", type);
        query.setParameter("expired", expired);
        return (List<Otp>) query.getResultList().stream().collect(Collectors.toList());
    }

    @Override
    public Otp save(Otp newOtp) {
        EntityManager entityManager = openEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(newOtp); // cascades the tool & skill relationships
        entityManager.getTransaction().commit();
        return newOtp;
    }
}
