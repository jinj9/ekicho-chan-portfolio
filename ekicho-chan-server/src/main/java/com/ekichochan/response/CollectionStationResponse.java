package com.ekichochan.response;

import java.time.Instant;

public record CollectionStationResponse(
        int id,
        String stationCd,
        String stationName,
        String stationEnglishName,
        Double latitude,
        Double longitude,
        int prefectureCd,
        String locationName,
        String appearanceRateCd,
        String stationInformationText,
        String imageData,
        String imageType,
        Instant firstObtainedAt,
        Instant lastObtainedAt,
        int obtainedCount,
        String firstObtainedMethod,
        String lastObtainedMethod,
        boolean alreadyObtained) {
}
