package com.ekichochan.controller;

import com.ekichochan.response.CollectionStationPageResponse;
import com.ekichochan.response.CollectionStationResponse;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.AccountStationCollectionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.ekichochan.request.LocationCollectionRequest;

@RestController
@RequestMapping("/api/v1/stations")
public class AccountStationCollectionController {
    private final AccountStationCollectionService service;

    public AccountStationCollectionController(AccountStationCollectionService service) {
        this.service = service;
    }

    @GetMapping("/gacha")
    public CollectionStationResponse gacha(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal) {
        return service.draw(principal.accountId());
    }

    @GetMapping("/collection")
    public CollectionStationPageResponse collection(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "48") int size) {
        return service.collection(principal.accountId(), page, size);
    }

    @PostMapping("/location-get")
    public CollectionStationResponse locationGet(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @Valid @RequestBody LocationCollectionRequest request) {
        return service.obtainAtLocation(principal.accountId(), request.stationId(),
                request.latitude(), request.longitude());
    }

    @GetMapping("/collection/{stationId}")
    public CollectionStationResponse detail(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @PathVariable int stationId) {
        return service.detail(principal.accountId(), stationId);
    }
}
