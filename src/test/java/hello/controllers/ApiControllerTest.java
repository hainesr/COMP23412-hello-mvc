package hello.controllers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import hello.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/greetings-init.sql")
public class ApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/greeting/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, %s!")));
	}

	@Test
	public void getHtmlGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/greeting/1").accept(MediaType.TEXT_HTML))
		.andExpect(status().isNotAcceptable());
	}

	@Test
	public void postGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/greeting").content("{ \"template\": \"Howdy, %s!\" }")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated()).andExpect(content().string(""))
		.andExpect(header().string("Location", containsString("/api/greeting/")));

		mvc.perform(MockMvcRequestBuilders.get("/api/greeting/2").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Howdy, %s!")));
	}

}
