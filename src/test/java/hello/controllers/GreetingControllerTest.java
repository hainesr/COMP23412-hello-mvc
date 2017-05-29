package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import hello.Hello;
import hello.dao.GreetingService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class GreetingControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private GreetingService greetingService;

	@Test
	public void getGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(content().string(containsString("Hello, World!"))).andExpect(view().name("greeting/show"));
	}

	@Test
	public void getGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, %s!")));
	}

	@Test
	public void getGreetingNameHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(content().string(containsString("Hello, Rob!")))
		.andExpect(view().name("greeting/show"));
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
		long before = greetingService.count();

		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "Howdy, %s!").accept(MediaType.TEXT_HTML))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/greeting"));

		long after = greetingService.count();

		assertThat((before + 1), equalTo(after));
	}

	@Test
	public void postGreetingJson() throws Exception {
		long before = greetingService.count();

		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated()).andExpect(content().string(""))
		.andExpect(header().string("Location", containsString("/greeting/"))).andReturn();

		long after = greetingService.count();

		assertThat((before + 1), equalTo(after));
	}

	@Test
	public void postBadGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "no placeholder").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new"));
	}

	@Test
	public void postBadGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"no placeholder\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""));
	}

	@Test
	public void postLongGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "abcdefghij %s klmnopqrst uvwxyz").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("greeting/new"));
	}

	@Test
	public void postLongGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"abcdefghij %s klmnopqrst uvwxyz\" }").accept(MediaType.APPLICATION_JSON))
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
