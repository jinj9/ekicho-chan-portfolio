package com.ekichochan.repositories;

import com.ekichochan.entity.AccountStationCollectionEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountStationCollectionRepository
        extends JpaRepository<AccountStationCollectionEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from AccountStationCollectionEntity c where c.accountId = :accountId and c.station.id = :stationId")
    Optional<AccountStationCollectionEntity> findForUpdate(
            @Param("accountId") UUID accountId, @Param("stationId") int stationId);

    @EntityGraph(attributePaths = "station")
    Page<AccountStationCollectionEntity> findByAccountId(UUID accountId, Pageable pageable);

    @EntityGraph(attributePaths = "station")
    Optional<AccountStationCollectionEntity> findByAccountIdAndStationId(UUID accountId, int stationId);
}
