package com.ekichochan.response;

import java.util.List;

public record ObtainedStationPageResponse(
        List<StationMasterWatchingResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
