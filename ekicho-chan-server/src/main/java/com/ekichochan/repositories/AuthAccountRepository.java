package com.ekichochan.repositories;

import com.ekichochan.entity.AuthAccountEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthAccountRepository extends JpaRepository<AuthAccountEntity, UUID> {
    boolean existsByNormalizedEmail(String normalizedEmail);
    Optional<AuthAccountEntity> findByNormalizedEmail(String normalizedEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from AuthAccountEntity account "
            + "where account.normalizedEmail = :normalizedEmail")
    Optional<AuthAccountEntity> findByNormalizedEmailForUpdate(
            @Param("normalizedEmail") String normalizedEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from AuthAccountEntity account where account.id = :accountId")
    Optional<AuthAccountEntity> findByIdForUpdate(@Param("accountId") UUID accountId);
}
