package com.ekichochan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_station_collection")
public class AccountStationCollectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private StationMasterEntity station;

    @Column(name = "first_obtained_at", nullable = false)
    private Instant firstObtainedAt;

    @Column(name = "last_obtained_at", nullable = false)
    private Instant lastObtainedAt;

    @Column(name = "obtained_count", nullable = false)
    private int obtainedCount;

    @Column(name = "first_obtained_method", nullable = false, length = 16)
    private String firstObtainedMethod;

    @Column(name = "last_obtained_method", nullable = false, length = 16)
    private String lastObtainedMethod;

    protected AccountStationCollectionEntity() {}

    public AccountStationCollectionEntity(UUID accountId, StationMasterEntity station, Instant obtainedAt) {
        this(accountId, station, obtainedAt, "UNKNOWN");
    }

    public AccountStationCollectionEntity(UUID accountId, StationMasterEntity station,
            Instant obtainedAt, String obtainedMethod) {
        this.accountId = accountId;
        this.station = station;
        this.firstObtainedAt = obtainedAt;
        this.lastObtainedAt = obtainedAt;
        this.obtainedCount = 1;
        this.firstObtainedMethod = obtainedMethod;
        this.lastObtainedMethod = obtainedMethod;
    }

    public void obtainAgain(Instant obtainedAt) {
        obtainAgain(obtainedAt, "UNKNOWN");
    }

    public void obtainAgain(Instant obtainedAt, String obtainedMethod) {
        lastObtainedAt = obtainedAt;
        lastObtainedMethod = obtainedMethod;
        obtainedCount++;
    }

    public Long getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public StationMasterEntity getStation() { return station; }
    public Instant getFirstObtainedAt() { return firstObtainedAt; }
    public Instant getLastObtainedAt() { return lastObtainedAt; }
    public int getObtainedCount() { return obtainedCount; }
    public String getFirstObtainedMethod() { return firstObtainedMethod; }
    public String getLastObtainedMethod() { return lastObtainedMethod; }
}
