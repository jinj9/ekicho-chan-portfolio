package com.ekichochan.controller;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.LocationGetRequest;
import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.service.StationMasterService;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public demo endpoints. They never write a user collection to the database. */
@RestController
@RequestMapping("/api/v1/portfolio/stations")
@ConditionalOnProperty(name = "app.portfolio-mode", havingValue = "true")
public class PortfolioStationController {
    private final StationMasterService stationMasterService;

    public PortfolioStationController(StationMasterService stationMasterService) {
        this.stationMasterService = stationMasterService;
    }

    @GetMapping("/gacha")
    public StationMasterResponse gacha() {
        return stationMasterService.getPortfolioGacha();
    }

    @PostMapping("/location-get")
    public StationMasterResponse locationGet(@RequestBody LocationGetRequest request)
            throws BadRequestException, IOException {
        return stationMasterService.getPortfolioLocation(request);
    }

    @GetMapping("/{stationId}")
    public StationMasterResponse station(
            @PathVariable int stationId,
            @RequestParam(name = "method", defaultValue = "GACHA") String method) {
        return stationMasterService.getPortfolioStation(stationId, "LOCATION".equals(method));
    }
}
