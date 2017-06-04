package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import hello.Hello;
import hello.dao.GreetingService;
import hello.entities.Greeting;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GreetingControllerTest {

	private MockMvc mvc;

	@Mock
	private Greeting greeting;

	@Mock
	private GreetingService greetingService;

	@InjectMocks
	private GreetingController greetingController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(greetingController).build();
	}

	@Test
	public void getEmptyGreetingsList() throws Exception {
		when(greetingService.findAll()).thenReturn(Collections.<Greeting> emptyList());

		mvc.perform(MockMvcRequestBuilders.get("/greeting").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/index")).andExpect(handler().methodName("list"));

		verifyZeroInteractions(greeting);
	}

	@Test
	public void getGreetingsList() throws Exception {
		when(greetingService.findAll()).thenReturn(Collections.<Greeting> singletonList(greeting));

		mvc.perform(MockMvcRequestBuilders.get("/greeting").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/index")).andExpect(handler().methodName("list"));

		verifyZeroInteractions(greeting);
	}

	@Test
	public void getGreeting() throws Exception {
		when(greetingService.findOne(1)).thenReturn(greeting);

		mvc.perform(MockMvcRequestBuilders.get("/greeting/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/show")).andExpect(handler().methodName("greeting"));

		verify(greeting).getGreeting("World");
	}

	@Test
	public void getGreetingName() throws Exception {
		when(greetingService.findOne(1)).thenReturn(greeting);

		mvc.perform(MockMvcRequestBuilders.get("/greeting/1?name=Rob").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("greeting/show"))
		.andExpect(handler().methodName("greeting"));

		verify(greeting).getGreeting("Rob");
	}

	@Test
	public void getNewGreeting() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/greeting/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new")).andExpect(handler().methodName("newGreeting"));
	}

	@Test
	public void postGreetingHtml() throws Exception {
		ArgumentCaptor<Greeting> arg = ArgumentCaptor.forClass(Greeting.class);

		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "Howdy, %s!").accept(MediaType.TEXT_HTML))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/greeting")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("createGreetingFromForm"));

		verify(greetingService).save(arg.capture());
		assertThat("Howdy, %s!", equalTo(arg.getValue().getTemplate()));
	}

	@Test
	public void postGreetingJson() throws Exception {
		ArgumentCaptor<Greeting> arg = ArgumentCaptor.forClass(Greeting.class);

		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated()).andExpect(content().string(""))
		.andExpect(header().string("Location", containsString("/greeting/")))
		.andExpect(handler().methodName("createGreetingFromJson"));

		verify(greetingService).save(arg.capture());
		assertThat("Howdy, %s!", equalTo(arg.getValue().getTemplate()));
	}

	@Test
	public void postBadGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "no placeholder").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new"))
		.andExpect(model().attributeHasFieldErrors("greeting", "template"))
		.andExpect(handler().methodName("createGreetingFromForm"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postBadGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"no placeholder\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""))
		.andExpect(handler().methodName("createGreetingFromJson"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postLongGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "abcdefghij %s klmnopqrst uvwxyz").accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("greeting/new"))
		.andExpect(model().attributeHasFieldErrors("greeting", "template"))
		.andExpect(handler().methodName("createGreetingFromForm"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postLongGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"abcdefghij %s klmnopqrst uvwxyz\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""))
		.andExpect(handler().methodName("createGreetingFromJson"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postEmptyGreetingHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("greeting/new"))
		.andExpect(model().attributeHasFieldErrors("greeting", "template"))
		.andExpect(handler().methodName("createGreetingFromForm"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postEmptyGreetingJson() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"\" }").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""))
		.andExpect(handler().methodName("createGreetingFromJson"));

		verify(greetingService, never()).save(greeting);
	}
}
