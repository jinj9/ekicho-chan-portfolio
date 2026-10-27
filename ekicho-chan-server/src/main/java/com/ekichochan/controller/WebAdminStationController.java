package com.ekichochan.controller;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.StationMasterImageDataRegistRequest;
import com.ekichochan.request.StationMasterRegistRequest;
import com.ekichochan.request.StationMasterSearchRequest;
import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.service.StationMasterService;
import com.ekichochan.service.AdminRequestValidator;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/web/admin/stations")
public class WebAdminStationController {
    private final StationMasterService stationMasterService;
    private final AdminRequestValidator validator;

    public WebAdminStationController(StationMasterService stationMasterService, AdminRequestValidator validator) {
        this.stationMasterService = stationMasterService;
        this.validator = validator;
    }

    @PostMapping("/search")
    public List<StationMasterResponse> search(@RequestBody StationMasterSearchRequest request) {
        return stationMasterService.getStationMasterSearch(request);
    }

    @GetMapping("/{id}")
    public StationMasterResponse get(@PathVariable("id") int id) {
        return stationMasterService.getStationMasterSearch(id);
    }

    @GetMapping("/{id}/image")
    public StationMasterResponse image(@PathVariable("id") int id) {
        return stationMasterService.getStationMasterAndImage(id);
    }

    @PostMapping
    public int create(@Valid @RequestBody StationMasterRegistRequest request) throws BadRequestException, IOException {
        validator.validateStation(request);
        return stationMasterService.registStationMaster(request);
    }

    @PutMapping("/{id}")
    public int update(@PathVariable("id") int id, @Valid @RequestBody StationMasterRegistRequest request)
            throws BadRequestException, IOException {
        validator.validateStation(request);
        return stationMasterService.updateStationMaster(id, request);
    }

    @PostMapping("/image")
    public int updateImage(@Valid @RequestBody StationMasterImageDataRegistRequest request)
            throws BadRequestException, IOException {
        validator.validateImages(request);
        return stationMasterService.updateStationMasterImageData(request);
    }
}
