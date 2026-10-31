package com.ekichochan.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ekichochan.response.StationMasterResponse;
import com.ekichochan.security.ApiSecurityConfig;
import com.ekichochan.service.StationMasterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PortfolioStationController.class, properties = {
        "spring.profiles.active=test", "app.portfolio-mode=true" })
@Import(ApiSecurityConfig.class)
@ContextConfiguration(classes = {PortfolioStationController.class, ApiSecurityConfig.class})
class PortfolioStationControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean StationMasterService service;

    @Test
    void portfolioEndpointsWorkWithoutLoginAndLocationPostDoesNotRequireCsrf() throws Exception {
        StationMasterResponse station = new StationMasterResponse();
        station.setId(1);
        station.setStationName("博多");
        when(service.getPortfolioGacha()).thenReturn(station);
        when(service.getPortfolioLocation(any())).thenReturn(station);

        mvc.perform(get("/api/v1/portfolio/stations/gacha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value("博多"));
        mvc.perform(post("/api/v1/portfolio/stations/location-get")
                .contentType("application/json")
                .content("{\"stationCd\":\"1\",\"latitude\":33.5,\"longitude\":130.4}"))
                .andExpect(status().isOk());

        verify(service).getPortfolioGacha();
        verify(service).getPortfolioLocation(any());
    }

    @Test
    void authenticationAndAdministrationAreClosedInPortfolioMode() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                .contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/web/auth/csrf"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/web/admin/stations/1"))
                .andExpect(status().isUnauthorized());
    }
}
