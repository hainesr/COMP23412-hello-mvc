package hello.controllers;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
public class GreetingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(content().string(containsString("Hello, World!"))).andExpect(view().name("greeting/show"));
	}

	@Test
	public void getGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, World!")));
	}

	@Test
	public void getGreetingNameHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, Rob!")))
		.andExpect(view().name("greeting/show"));
	}

	@Test
	public void getGreetingNameJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, Rob!")));
	}

	@Test
	public void getNewGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new"));
	}

	@Test
	public void getNewGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/new").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotAcceptable());
	}

	@Test
	public void postGreetingHtml() throws Exception {
		String greeting = "Howdy, %s!";
		String greetingResult = String.format(greeting, "World");

		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", greeting).accept(MediaType.TEXT_HTML))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/greeting"));

		mvc.perform(MockMvcRequestBuilders.get("/greeting/2").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(content().string(containsString(greetingResult)));
	}

	@Test
	public void postGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated()).andExpect(content().string(""))
		.andExpect(header().string("Location", containsString("/greeting/2")));

		mvc.perform(MockMvcRequestBuilders.get("/greeting/2").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Howdy, World!")));
	}

	@Test
	public void postLongGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "012345678901234567890123456789A").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("greeting/new"));
	}

	@Test
	public void postLongGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"012345678901234567890123456789A\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""));
	}

	@Test
	public void postEmptyGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new"));
	}

	@Test
	public void postEmptyGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""));
	}
}
