package hello.controllers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.net.URLEncoder;

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
public class GreetingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(content().string(containsString("Hello, World!"))).andExpect(view().name("greeting/show"));
	}

	@Test
	public void getJsonGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotAcceptable());
	}

	@Test
	public void getGreetingName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, Rob!")))
		.andExpect(view().name("greeting/show"));
	}

	@Test
	public void getJsonGreetingName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotAcceptable());
	}

	@Test
	public void postGreeting() throws Exception {
		String greeting = "Howdy!";
		String encodedGreeting = "template=" + URLEncoder.encode(greeting, "UTF-8");

		mvc.perform(MockMvcRequestBuilders.post("/greeting").content(encodedGreeting)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(content().string(containsString(greeting)))
		.andExpect(view().name("greeting/index"));
	}

	@Test
	public void postJsonGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").content("{ \"template\": \"Howdy, %s!\" }")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_HTML))
		.andExpect(status().isUnsupportedMediaType());
	}
}
