package hello.controllers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import hello.Application;
import hello.models.Greeting;
import hello.services.GreetingService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApiControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private GreetingService greetingService;

	@Before
	public void setUp() {
		greetingService.save(new Greeting("Hello, %s!"));
	}

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

		mvc.perform(MockMvcRequestBuilders.get("/api/greeting/4").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Howdy, %s!")));
	}

}
