package com.ekichochan.security;

import com.ekichochan.controller.EkichoChanController;
import com.ekichochan.service.StationMasterService;
import com.ekichochan.response.StationMasterResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EkichoChanController.class, properties = "spring.profiles.active=test")
@Import(ApiSecurityConfig.class)
@org.springframework.test.context.ContextConfiguration(classes = {EkichoChanController.class, ApiSecurityConfig.class})
class ApiSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean StationMasterService service;

    @Test void publicReadsStillReturnJson() throws Exception {
        StationMasterResponse station = new StationMasterResponse();
        station.setId(1);
        station.setStationName("博多");
        when(service.getStationMasterList("40")).thenReturn(List.of(station));
        when(service.getStationMasterSearch(1)).thenReturn(station);
        when(service.getStationMasterAndImage(1)).thenReturn(station);
        mvc.perform(get("/api/stationMaster/store/stationList/40"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].stationName").value("博多"));
        mvc.perform(get("/api/stationMaster/search/1")).andExpect(status().isOk());
        mvc.perform(get("/api/stationMaster/search/image/1")).andExpect(status().isOk());
    }

    @Test void legacyGetAcquisitionAndHistoryAreBlocked() throws Exception {
        mvc.perform(get("/api/stationMaster/gacha/someone"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
        mvc.perform(get("/api/stationMaster/watchingData/someone")).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test void evenAuthenticatedRequestsCannotUseLegacyWrites() throws Exception {
        for (String path : List.of("locationGet", "regist", "imageUpdate", "search")) {
            mvc.perform(post("/api/stationMaster/" + path).with(user("test")).with(csrf())
                    .contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
        }
        mvc.perform(put("/api/stationMaster/update/1").with(user("test")).with(csrf())
                .contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/stationMaster/gacha/test").with(user("test")))
            .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test void unknownRoutesAndDefaultLoginDoNotOpenABackdoor() throws Exception {
        mvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/login")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/stationMaster/locationGet").contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }
}
