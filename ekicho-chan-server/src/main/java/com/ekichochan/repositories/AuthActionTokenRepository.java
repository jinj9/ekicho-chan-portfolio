package com.ekichochan.repositories;

import com.ekichochan.entity.AuthActionTokenEntity;
import com.ekichochan.entity.AuthActionTokenPurpose;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.time.Instant;
import org.springframework.data.repository.query.Param;

public interface AuthActionTokenRepository extends JpaRepository<AuthActionTokenEntity, UUID> {
    Optional<AuthActionTokenEntity> findFirstByAccount_IdAndPurposeOrderByCreatedAtDesc(
            UUID accountId, AuthActionTokenPurpose purpose);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from AuthActionTokenEntity token "
            + "join fetch token.account where token.tokenDigest = :digest and token.purpose = :purpose")
    Optional<AuthActionTokenEntity> findForUpdate(
            @Param("digest") byte[] digest,
            @Param("purpose") AuthActionTokenPurpose purpose);

    @Modifying
    @Query("update AuthActionTokenEntity token set token.usedAt = :usedAt "
            + "where token.account.id = :accountId and token.purpose = :purpose and token.usedAt is null")
    int invalidateUnused(@Param("accountId") UUID accountId,
            @Param("purpose") AuthActionTokenPurpose purpose,
            @Param("usedAt") Instant usedAt);
}
