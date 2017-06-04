package hello.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Optional;

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
@WebMvcTest(GreetingController.class)
@Import(Security.class)
public class GreetingControllerTest {

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

		mvc.perform(get("/greetings").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("greetings/index")).andExpect(handler().methodName("list"));
	}

	@Test
	public void getGreetingsList() throws Exception {
		when(greetingService.findAll()).thenReturn(Collections.<Greeting>singletonList(greeting));

		mvc.perform(get("/greetings").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("greetings/index")).andExpect(handler().methodName("list"));

		verify(greeting, atLeastOnce()).getId();
		verify(greeting).getTemplate();
	}

	@Test
	public void getGreeting() throws Exception {
		when(greetingService.findById(1)).thenReturn(Optional.of(greeting));

		mvc.perform(get("/greetings/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("greetings/show")).andExpect(handler().methodName("greeting"));

		verify(greeting).getGreeting("World");
	}

	@Test
	public void getGreetingName() throws Exception {
		when(greetingService.findById(1)).thenReturn(Optional.of(greeting));

		mvc.perform(get("/greetings/1?name=Rob").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("greetings/show")).andExpect(handler().methodName("greeting"));

		verify(greeting).getGreeting("Rob");
	}

	@Test
	public void getGreetingNotFound() throws Exception {
		when(greetingService.findById(1)).thenReturn(Optional.empty());

		mvc.perform(get("/greetings/1").accept(MediaType.TEXT_HTML)).andExpect(status().isNotFound())
				.andExpect(handler().methodName("greeting")).andExpect(view().name("greetings/not_found"));
	}

	@Test
	public void getNewGreetingNoAuth() throws Exception {
		mvc.perform(get("/greetings/new").accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
				.andExpect(header().string("Location", endsWith("/sign-in")));
	}

	@Test
	public void getNewGreeting() throws Exception {
		mvc.perform(get("/greetings/new").with(user("Rob").roles(Security.ADMIN_ROLE)).accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk()).andExpect(view().name("greetings/new"))
				.andExpect(handler().methodName("newGreeting"));
	}

	@Test
	public void postGreetingNoAuth() throws Exception {
		mvc.perform(post("/greetings").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("template", "Howdy, %s!").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(header().string("Location", endsWith("/sign-in")));

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void postGreetingBadRole() throws Exception {
		mvc.perform(
				post("/greetings").with(user("Rob").roles(BAD_ROLE)).contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("template", "Howdy, %s!").accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(status().isForbidden());

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void postGreetingNoCsrf() throws Exception {
		mvc.perform(post("/greetings").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("template", "Howdy, %s!")
				.accept(MediaType.TEXT_HTML)).andExpect(status().isForbidden());

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void postGreeting() throws Exception {
		ArgumentCaptor<Greeting> arg = ArgumentCaptor.forClass(Greeting.class);
		when(greetingService.save(any(Greeting.class))).then(returnsFirstArg());

		mvc.perform(post("/greetings").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("template", "Howdy, %s!")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound()).andExpect(content().string(""))
				.andExpect(view().name("redirect:/greetings")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("createGreeting")).andExpect(flash().attributeExists("ok_message"));

		verify(greetingService).save(arg.capture());
		assertThat("Howdy, %s!", equalTo(arg.getValue().getTemplate()));
	}

	@Test
	public void postBadGreeting() throws Exception {
		mvc.perform(post("/greetings").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("template", "no placeholder")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("greetings/new"))
				.andExpect(model().attributeHasFieldErrors("greeting", "template"))
				.andExpect(handler().methodName("createGreeting")).andExpect(flash().attributeCount(0));

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void postLongGreeting() throws Exception {
		mvc.perform(post("/greetings").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("template", "abcdefghij %s klmnopqrst uvwxyz")
				.accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("greetings/new"))
				.andExpect(model().attributeHasFieldErrors("greeting", "template"))
				.andExpect(handler().methodName("createGreeting")).andExpect(flash().attributeCount(0));

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void postEmptyGreeting() throws Exception {
		mvc.perform(post("/greetings").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("template", "").accept(MediaType.TEXT_HTML)
				.with(csrf())).andExpect(status().isOk()).andExpect(view().name("greetings/new"))
				.andExpect(model().attributeHasFieldErrors("greeting", "template"))
				.andExpect(handler().methodName("createGreeting")).andExpect(flash().attributeCount(0));

		verify(greetingService, never()).save(any(Greeting.class));
	}

	@Test
	public void deleteGreeting() throws Exception {
		mvc.perform(delete("/greetings/1").accept(MediaType.TEXT_HTML).with(csrf())).andExpect(status().isFound())
				.andExpect(view().name("redirect:/greetings")).andExpect(handler().methodName("deleteGreeting"));

		verify(greetingService).deleteById(1);
	}
}
