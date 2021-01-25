package hello.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hello.Hello;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class GreetingControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Pattern CSRF = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
	private static String SESSION_KEY = "JSESSIONID";

	@LocalServerPort
	private int port;

	private int currentRows;

	private WebTestClient client;

	@BeforeEach
	public void setUp() {
		currentRows = countRowsInTable("greeting");
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void getGreeting() {
		client.get().uri("/greeting/1").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectHeader()
				.contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class).consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Hello, World!"));
				});
	}

	@Test
	public void getGreetingName() {
		client.get().uri("/greeting/1?name=Rob").accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML).expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("Hello, Rob!"));
				});
	}

	@Test
	public void getNewGreetingNoUser() {
		// Should redirect to the sign-in page.
		client.get().uri("/greeting/new").accept(MediaType.TEXT_HTML).exchange().expectStatus().isFound().expectHeader()
				.value("Location", endsWith("/sign-in"));
	}

	@Test
	public void getNewGreetingWithUser() {
		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().get().uri("/greeting/new")
				.accept(MediaType.TEXT_HTML).exchange().expectStatus().isOk().expectBody(String.class)
				.consumeWith(result -> {
					assertThat(result.getResponseBody(), containsString("_csrf"));
				});
	}

	@Test
	public void postGreetingNoUser() {
		String[] tokens = login();

		// Attempt to POST a valid greeting.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("template", "Howdy, %s!");

		// We don't set the session ID, so have no credentials.
		// This should redirect to the sign-in page.
		client.post().uri("/greeting").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).exchange().expectStatus().isFound().expectHeader()
				.value("Location", endsWith("/sign-in"));

		// Check nothing added to the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void postGreetingWithUser() {
		String[] tokens = login();

		// Attempt to POST a valid greeting.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", tokens[0]);
		form.add("template", "Howdy, %s!");

		// The session ID cookie holds our login credentials.
		client.post().uri("/greeting").accept(MediaType.TEXT_HTML).contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.bodyValue(form).cookies(cookies -> {
					cookies.add(SESSION_KEY, tokens[1]);
				}).exchange().expectStatus().isFound().expectHeader().value("Location", endsWith("/greeting"));

		// Check one row is added to the database.
		assertThat(currentRows + 1, equalTo(countRowsInTable("greeting")));
	}

	private String[] login() {
		String[] tokens = new String[2];

		// Although this doesn't POST the log in form it effectively logs us in.
		// If we provide the correct credentials here, we get a session ID back which
		// keeps us logged in.
		EntityExchangeResult<String> result = client.mutate().filter(basicAuthentication("Rob", "Haines")).build().get()
				.uri("/").accept(MediaType.TEXT_HTML).exchange().expectBody(String.class).returnResult();
		tokens[0] = getCsrfToken(result.getResponseBody());
		tokens[1] = result.getResponseCookies().getFirst(SESSION_KEY).getValue();

		return tokens;
	}

	private String getCsrfToken(String body) {
		Matcher matcher = CSRF.matcher(body);

		// matcher.matches() must be called; might as well assert something as well...
		assertThat(matcher.matches(), equalTo(true));

		return matcher.group(1);
	}
}
