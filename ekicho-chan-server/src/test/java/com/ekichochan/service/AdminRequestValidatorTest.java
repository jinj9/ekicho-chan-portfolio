package com.ekichochan.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ekichochan.exception.BadRequestException;
import com.ekichochan.request.StationMasterImageDataRegistRequest;
import com.ekichochan.request.StationMasterRegistRequest;
import org.junit.jupiter.api.Test;

class AdminRequestValidatorTest {
    private final AdminRequestValidator validator = new AdminRequestValidator();

    @Test
    void rejectsCoordinatesOutsideGeographicRange() {
        StationMasterRegistRequest request = new StationMasterRegistRequest();
        request.setLatitude("90.0000001");
        request.setLongitude("130.4");
        assertThrows(BadRequestException.class, () -> validator.validateStation(request));
    }

    @Test
    void acceptsValidCoordinates() {
        StationMasterRegistRequest request = new StationMasterRegistRequest();
        request.setLatitude("33.5893588");
        request.setLongitude("130.4207079");
        assertDoesNotThrow(() -> validator.validateStation(request));
    }

    @Test
    void rejectsContentThatDoesNotMatchImageType() {
        StationMasterImageDataRegistRequest request = new StationMasterImageDataRegistRequest();
        request.setImageData("dGV4dA==");
        request.setImageType("png");
        request.setImageDataSP("");
        request.setImageTypeSP("");
        assertThrows(BadRequestException.class, () -> validator.validateImages(request));
    }
}
