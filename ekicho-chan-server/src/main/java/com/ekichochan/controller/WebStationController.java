package com.ekichochan.controller;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.LocationGetRequest;
import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.response.StationMasterWatchingResponse;
import com.ekichochan.response.ObtainedStationPageResponse;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.StationMasterService;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

/** Webセッションのユーザーに紐づく駅画像API。ユーザーIDはリクエストから受け取らない。 */
@RestController
@RequestMapping("/api/v1/web/stations")
public class WebStationController {
    private final StationMasterService stationMasterService;

    public WebStationController(StationMasterService stationMasterService) {
        this.stationMasterService = stationMasterService;
    }

    @GetMapping("/gacha")
    public StationMasterResponse gacha(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal) {
        return stationMasterService.getStationMasterSerch(principal.accountId().toString());
    }

    @PostMapping("/location-get")
    public StationMasterResponse locationGet(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @RequestBody LocationGetRequest request) throws BadRequestException, IOException {
        request.setUserId(principal.accountId().toString());
        return stationMasterService.getStationMasterSerch(request);
    }

    @GetMapping("/obtained")
    public ObtainedStationPageResponse obtained(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "prefectureCd", defaultValue = "0") int prefectureCd,
            @RequestParam(name = "stationName", defaultValue = "") String stationName,
            @RequestParam(name = "appearanceRateCd", defaultValue = "") String appearanceRateCd,
            @RequestParam(name = "sort", defaultValue = "newest") String sort) {
        return stationMasterService.getWatchingStationDataPage(
                principal.accountId().toString(), page, size, prefectureCd,
                stationName, appearanceRateCd, sort);
    }
}
