package hello.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.net.MalformedURLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.reactive.server.WebTestClient;

import hello.Hello;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class GreetingControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private int currentRows;

	private WebTestClient client;

	@BeforeEach
	public void setUp() throws MalformedURLException {
		currentRows = countRowsInTable("greeting");
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	public void getGreetingsList() {
		client.get().uri("/api/greetings").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.length()")
				.isEqualTo(currentRows);
	}

	@Test
	public void getGreeting() {
		client.get().uri("/api/greetings/1").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.template")
				.isEqualTo("Hello, %s!").jsonPath("$._links.self.href").value(endsWith("/1"));
	}

	@Test
	public void getGreetingNotFound() {
		client.get().uri("/api/greetings/99").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.error")
				.value(containsString("greeting 99")).jsonPath("$.id").isEqualTo("99");
	}

	@Test
	public void postGreetingNoUser() {
		// Attempt to POST a valid greeting.
		client.post().uri("/api/greetings").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{ \"template\": \"Howdy, %s!\" }").exchange().expectStatus().isUnauthorized();

		// Check nothing added to the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void postGreetingBadUser() {
		// Attempt to POST a valid greeting.
		client.mutate().filter(basicAuthentication("Bad", "Person")).build().post().uri("/api/greetings")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{ \"template\": \"Howdy, %s!\" }").exchange().expectStatus().isUnauthorized();

		// Check nothing added to the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void postGreetingWithUser() {
		// Attempt to POST a valid greeting.
		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().post().uri("/api/greetings")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{ \"template\": \"Howdy, %s!\" }").exchange().expectStatus().isCreated().expectHeader()
				.value("Location", containsString("/api/greetings")).expectBody().isEmpty();

		// Check one row is added to the database.
		assertThat(currentRows + 1, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void deleteGreetingNoUser() {
		int currentRows = countRowsInTable("greeting");

		client.delete().uri("/api/greetings/1").accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
				.isUnauthorized();

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void deleteGreetingBadUser() {
		int currentRows = countRowsInTable("greeting");

		client.mutate().filter(basicAuthentication("Bad", "Person")).build().delete().uri("/api/greetings/1")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isUnauthorized();

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void deleteGreetingWithUser() {
		int currentRows = countRowsInTable("greeting");

		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().delete().uri("/api/greetings/1")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNoContent().expectBody().isEmpty();

		// Check that one row is removed from the database.
		assertThat(currentRows - 1, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void deleteGreetingNotFound() {
		int currentRows = countRowsInTable("greeting");

		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().delete().uri("/api/greetings/99")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNotFound().expectBody()
				.jsonPath("$.error").value(containsString("greeting 99")).jsonPath("$.id").isEqualTo("99");

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void deleteAllGreetingsNoUser() {
		int currentRows = countRowsInTable("greeting");

		client.delete().uri("/api/greetings").accept(MediaType.APPLICATION_JSON).exchange().expectStatus()
				.isUnauthorized();

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void deleteAllGreetingsBadUser() {
		int currentRows = countRowsInTable("greeting");

		client.mutate().filter(basicAuthentication("Bad", "Person")).build().delete().uri("/api/greetings")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isUnauthorized();

		// Check nothing is removed from the database.
		assertThat(currentRows, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void deleteAllGreetingsWithUser() {
		client.mutate().filter(basicAuthentication("Rob", "Haines")).build().delete().uri("/api/greetings")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNoContent().expectBody().isEmpty();

		// Check that all rows are removed from the database.
		assertThat(0, equalTo(countRowsInTable("greeting")));
	}
}
