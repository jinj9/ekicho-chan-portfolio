package com.ekichochan.service;

import com.ekichochan.common.Enum;
import com.ekichochan.entity.AccountStationCollectionEntity;
import com.ekichochan.entity.ImageEntity;
import com.ekichochan.entity.StationMasterEntity;
import com.ekichochan.exception.StationDataNotFoundException;
import com.ekichochan.exception.BadRequestException;
import com.ekichochan.repositories.AccountStationCollectionRepository;
import com.ekichochan.repositories.ImageRepository;
import com.ekichochan.repositories.StationMasterRepository;
import com.ekichochan.response.CollectionStationPageResponse;
import com.ekichochan.response.CollectionStationResponse;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountStationCollectionService {
    private final AccountStationCollectionRepository collections;
    private final StationMasterRepository stations;
    private final ImageRepository images;
    private final Clock clock;
    private final Random random = new Random();

    public AccountStationCollectionService(AccountStationCollectionRepository collections,
            StationMasterRepository stations, ImageRepository images, Clock clock) {
        this.collections = collections;
        this.stations = stations;
        this.images = images;
        this.clock = clock;
    }

    @Transactional
    public CollectionStationResponse draw(UUID accountId) {
        List<StationMasterEntity> candidates = stations
                .getStationMasterEntityListByAppearanceRataCd(appearanceRateCode());
        if (candidates.isEmpty()) throw new StationDataNotFoundException("ガチャ対象の駅が登録されていません。");
        StationMasterEntity station = candidates.get(random.nextInt(candidates.size()));
        Instant now = clock.instant();
        var existing = collections.findForUpdate(accountId, station.getId());
        boolean alreadyObtained = existing.isPresent();
        AccountStationCollectionEntity collection = existing.orElseGet(
                () -> new AccountStationCollectionEntity(accountId, station, now, "GACHA"));
        if (alreadyObtained) collection.obtainAgain(now, "GACHA");
        collection = collections.saveAndFlush(collection);
        return toResponse(collection, alreadyObtained, false);
    }

    @Transactional
    public CollectionStationResponse obtainAtLocation(UUID accountId, int stationId,
            double latitude, double longitude) {
        StationMasterEntity station = stations.findById(stationId)
                .orElseThrow(() -> new StationDataNotFoundException("駅が見つかりません。"));
        if (station.getLatitude() == null || station.getLongitude() == null) {
            throw new StationDataNotFoundException("駅の位置情報が登録されていません。");
        }
        double distance = distanceInMeters(latitude, longitude,
                station.getLatitude(), station.getLongitude());
        if (distance > Enum.SUCCESSDISTANCE) {
            throw new BadRequestException("駅から約" + Math.round(distance)
                    + "m離れています。100m以内でGETしてください。");
        }
        ImageEntity specialImage = images.getImageData(station.getStationCd(), true);
        if (specialImage == null || specialImage.getImageData() == null
                || specialImage.getImageData().length == 0) {
            throw new StationDataNotFoundException("位置情報GET用の画像が登録されていません。");
        }
        Instant now = clock.instant();
        var existing = collections.findForUpdate(accountId, stationId);
        boolean alreadyObtained = existing.isPresent();
        AccountStationCollectionEntity collection = existing.orElseGet(
                () -> new AccountStationCollectionEntity(accountId, station, now, "LOCATION"));
        if (alreadyObtained) collection.obtainAgain(now, "LOCATION");
        collection = collections.saveAndFlush(collection);
        return toResponse(collection, alreadyObtained, true);
    }

    @Transactional(readOnly = true)
    public CollectionStationPageResponse collection(UUID accountId, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(48, Math.max(1, size));
        Page<AccountStationCollectionEntity> result = collections.findByAccountId(accountId,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "lastObtainedAt")));
        return new CollectionStationPageResponse(result.getContent().stream()
                .map(item -> toResponse(item, true, false)).toList(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public CollectionStationResponse detail(UUID accountId, int stationId) {
        return collections.findByAccountIdAndStationId(accountId, stationId)
                .map(item -> toResponse(item, true, false))
                .orElseThrow(() -> new StationDataNotFoundException("獲得済みの駅が見つかりません。"));
    }

    private CollectionStationResponse toResponse(AccountStationCollectionEntity collection,
            boolean alreadyObtained, boolean specialImage) {
        StationMasterEntity station = collection.getStation();
        ImageEntity image = images.getImageData(station.getStationCd(), specialImage);
        return new CollectionStationResponse(station.getId(), station.getStationCd(), station.getStationName(),
                station.getStationEnglishName(), station.getLatitude(), station.getLongitude(),
                station.getPrefectureCd(), Enum.PREFECTURE.getByCode(station.getPrefectureCd()),
                station.getAppearanceRateCd(), station.getStationInformationText(),
                image == null || image.getImageData() == null ? null : Base64.getEncoder().encodeToString(image.getImageData()),
                image == null ? null : image.getImageType(), collection.getFirstObtainedAt(),
                collection.getLastObtainedAt(), collection.getObtainedCount(),
                collection.getFirstObtainedMethod(), collection.getLastObtainedMethod(), alreadyObtained);
    }

    private String appearanceRateCode() {
        int value = random.nextInt(1000);
        if (LocalDate.now(clock).getDayOfWeek() == DayOfWeek.MONDAY && value <= 8) return "5";
        if (value <= 599) return "1";
        if (value <= 849) return "2";
        if (value <= 949) return "3";
        if (value <= 998) return "4";
        return "5";
    }

    private double distanceInMeters(double latitude1, double longitude1,
            double latitude2, double longitude2) {
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double longitudeDelta = Math.toRadians(longitude2 - longitude1);
        double cosine = Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(longitudeDelta);
        return 6_378_140d * Math.acos(Math.max(-1d, Math.min(1d, cosine)));
    }
}
