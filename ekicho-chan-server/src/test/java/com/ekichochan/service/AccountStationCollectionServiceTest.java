package com.ekichochan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ekichochan.entity.AccountStationCollectionEntity;
import com.ekichochan.entity.ImageEntity;
import com.ekichochan.entity.StationMasterEntity;
import com.ekichochan.repositories.AccountStationCollectionRepository;
import com.ekichochan.repositories.ImageRepository;
import com.ekichochan.repositories.StationMasterRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AccountStationCollectionServiceTest {
    private static final Instant NOW = Instant.parse("2026-09-12T01:02:03Z");
    private final AccountStationCollectionRepository collections = mock(AccountStationCollectionRepository.class);
    private final StationMasterRepository stations = mock(StationMasterRepository.class);
    private final ImageRepository images = mock(ImageRepository.class);
    private final UUID accountId = UUID.randomUUID();
    private final StationMasterEntity station = new StationMasterEntity();
    private AccountStationCollectionService service;

    @BeforeEach
    void setUp() {
        station.setId(10);
        station.setStationCd("station-10");
        station.setStationName("テスト駅");
        station.setPrefectureCd(40);
        when(stations.getStationMasterEntityListByAppearanceRataCd(anyString())).thenReturn(List.of(station));
        ImageEntity image = new ImageEntity();
        image.setImageData(new byte[] {1, 2, 3});
        image.setImageType("image/png");
        when(images.getImageData("station-10", false)).thenReturn(image);
        when(collections.saveAndFlush(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new AccountStationCollectionService(collections, stations, images,
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void firstDrawCreatesOneCollectionEntry() {
        when(collections.findForUpdate(accountId, 10)).thenReturn(Optional.empty());

        var response = service.draw(accountId);

        assertFalse(response.alreadyObtained());
        assertEquals(1, response.obtainedCount());
        assertEquals(NOW, response.firstObtainedAt());
        assertEquals(NOW, response.lastObtainedAt());
        assertEquals("GACHA", response.firstObtainedMethod());
        assertEquals("GACHA", response.lastObtainedMethod());
        ArgumentCaptor<AccountStationCollectionEntity> saved =
                ArgumentCaptor.forClass(AccountStationCollectionEntity.class);
        verify(collections).saveAndFlush(saved.capture());
        assertEquals(accountId, saved.getValue().getAccountId());
        assertSame(station, saved.getValue().getStation());
    }

    @Test
    void repeatedDrawUpdatesExistingEntryWithoutCreatingAnother() {
        var existing = new AccountStationCollectionEntity(accountId, station,
                Instant.parse("2026-09-01T00:00:00Z"));
        when(collections.findForUpdate(accountId, 10)).thenReturn(Optional.of(existing));

        var response = service.draw(accountId);

        assertTrue(response.alreadyObtained());
        assertEquals(2, response.obtainedCount());
        assertEquals(Instant.parse("2026-09-01T00:00:00Z"), response.firstObtainedAt());
        assertEquals(NOW, response.lastObtainedAt());
        assertEquals("UNKNOWN", response.firstObtainedMethod());
        assertEquals("GACHA", response.lastObtainedMethod());
        verify(collections).saveAndFlush(existing);
    }

    @Test
    void detailIsAlwaysScopedToAuthenticatedAccount() {
        when(collections.findByAccountIdAndStationId(accountId, 10)).thenReturn(Optional.of(
                new AccountStationCollectionEntity(accountId, station, NOW)));

        service.detail(accountId, 10);

        verify(collections).findByAccountIdAndStationId(accountId, 10);
    }
}
