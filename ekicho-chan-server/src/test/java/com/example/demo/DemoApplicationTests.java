package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.ekichochan.EkichoChanApplication.class, properties = {
    "spring.profiles.active=test",
    "spring.datasource.url=jdbc:h2:mem:contexttest;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.flyway.enabled=false",
    "spring.mail.host=localhost",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.sql.init.mode=never"
})
@AutoConfigureMockMvc
class DemoApplicationTests {
	@Autowired MockMvc mvc;

	@Test
	void contextLoads() {
	}

	@Test
	void healthIsPublicAndReportsDatabaseAvailability() throws Exception {
		mvc.perform(get("/actuator/health"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UP"));
	}

	@Test
	void sensitiveActuatorEndpointsAreNotPublic() throws Exception {
		mvc.perform(get("/actuator/env"))
			.andExpect(status().isUnauthorized());
	}

}
