package com.ekichochan.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ekichochan.response.ObtainedStationPageResponse;
import com.ekichochan.entity.AuthAccountRole;
import com.ekichochan.security.ApiSecurityConfig;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.StationMasterService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WebStationController.class, properties = "spring.profiles.active=test")
@Import(ApiSecurityConfig.class)
@ContextConfiguration(classes = {WebStationController.class, ApiSecurityConfig.class})
class WebStationControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean StationMasterService service;

    @Test
    void obtainedBindsAllQueryParameters() throws Exception {
        UUID accountId = UUID.randomUUID();
        AuthenticatedAccountPrincipal principal = new AuthenticatedAccountPrincipal(
                accountId, "test@invalid.example", "テスト", AuthAccountRole.USER);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of());
        when(service.getWatchingStationDataPage(
                eq(accountId.toString()), eq(1), eq(12), eq(40), eq("博多"), eq("5"), eq("station")))
                .thenReturn(new ObtainedStationPageResponse(List.of(), 1, 12, 0, 0));

        mvc.perform(get("/api/v1/web/stations/obtained")
                .param("page", "1")
                .param("size", "12")
                .param("prefectureCd", "40")
                .param("stationName", "博多")
                .param("appearanceRateCd", "5")
                .param("sort", "station")
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.content").isArray());
    }
}
