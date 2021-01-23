package hello.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import hello.config.Security;
import hello.dao.GreetingService;
import hello.entities.Greeting;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GreetingControllerApi.class)
@Import(Security.class)
public class GreetingControllerApiTest {

	private final static String BAD_ROLE = "USER";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GreetingService greetingService;

	@Mock
	private Greeting greeting;

	@Test
	public void getEmptyGreetingsList() throws Exception {
		when(greetingService.findAll()).thenReturn(Collections.<Greeting>emptyList());

		mvc.perform(get("/api/greeting").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("list")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/greeting")));
	}

	@Test
	public void getGreetingsList() throws Exception {
		Greeting g = new Greeting("%s");
		when(greetingService.findAll()).thenReturn(Collections.<Greeting>singletonList(g));

		mvc.perform(get("/api/greeting").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("list")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/greeting")))
				.andExpect(jsonPath("$._embedded.greetings.length()", equalTo(1)));
	}

	@Test
	public void getGreeting() throws Exception {
		int id = 0;
		Greeting g = new Greeting("%s");
		when(greetingService.findOne(id)).thenReturn(g);

		mvc.perform(get("/api/greeting/{id}", id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(handler().methodName("greeting")).andExpect(jsonPath("$.template", equalTo("%s")))
				.andExpect(jsonPath("$._links.self.href", endsWith("" + id)));
	}

	@Test
	public void getNewGreeting() throws Exception {
		mvc.perform(get("/api/greeting/new").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable())
				.andExpect(handler().methodName("newGreeting"));
	}

	@Test
	public void postGreetingNoAuth() throws Exception {
		mvc.perform(post("/api/greeting").contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postGreetingBadAuth() throws Exception {
		mvc.perform(post("/api/greeting").with(anonymous()).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postGreetingBadRole() throws Exception {
		mvc.perform(post("/api/greeting").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"Howdy, %s!\" }").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postGreeting() throws Exception {
		ArgumentCaptor<Greeting> arg = ArgumentCaptor.forClass(Greeting.class);

		mvc.perform(post("/api/greeting").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_JSON).content("{ \"template\": \"Howdy, %s!\" }")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(content().string(""))
				.andExpect(header().string("Location", containsString("/api/greeting/")))
				.andExpect(handler().methodName("createGreeting"));

		verify(greetingService).save(arg.capture());
		assertThat("Howdy, %s!", equalTo(arg.getValue().getTemplate()));
	}

	@Test
	public void postBadGreeting() throws Exception {
		mvc.perform(post("/api/greeting").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_JSON).content("{ \"template\": \"no placeholder\" }")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
				.andExpect(content().string("")).andExpect(handler().methodName("createGreeting"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postLongGreeting() throws Exception {
		mvc.perform(post("/api/greeting").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"template\": \"abcdefghij %s klmnopqrst uvwxyz\" }").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity()).andExpect(content().string(""))
				.andExpect(handler().methodName("createGreeting"));

		verify(greetingService, never()).save(greeting);
	}

	@Test
	public void postEmptyGreeting() throws Exception {
		mvc.perform(post("/api/greeting").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_JSON).content("{ \"template\": \"\" }")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnprocessableEntity())
				.andExpect(content().string("")).andExpect(handler().methodName("createGreeting"));

		verify(greetingService, never()).save(greeting);
	}
}
