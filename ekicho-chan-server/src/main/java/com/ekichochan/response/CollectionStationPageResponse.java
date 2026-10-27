package com.ekichochan.response;

import java.util.List;

public record CollectionStationPageResponse(
        List<CollectionStationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
