package hello.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;

import java.net.MalformedURLException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import hello.Hello;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class GreetingControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private String baseUrl;
	private String greetingUrl;

	private static final String INDEX = "/1";

	private HttpEntity<String> httpEntity;

	// An anonymous log in and a couple of users for basic auth.
	private final TestRestTemplate anon = new TestRestTemplate();
	private final TestRestTemplate evil = new TestRestTemplate("Bad", "Person");
	private final TestRestTemplate user = new TestRestTemplate("Rob", "Haines");

	@BeforeEach
	public void setUp() throws MalformedURLException {
		this.baseUrl = "http://localhost:" + port + "/api/greeting";
		this.greetingUrl = baseUrl + INDEX;

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void getGreeting() {
		get(greetingUrl, "Hello, %s!");
	}

	@Test
	public void postGreetingNoAuth() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		ResponseEntity<String> response = anon.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
		assertThat(2, equalTo(countRowsInTable("greeting")));
	}

	@Test
	public void postGreetingBadAuth() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		ResponseEntity<String> response = evil.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
		assertThat(2, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void postGreeting() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		ResponseEntity<String> response = user.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
		assertThat(response.getHeaders().getLocation().toString(), containsString(baseUrl));
		assertThat(response.getBody(), equalTo(null));
		assertThat(3, equalTo(countRowsInTable("greeting")));
	}

	private void get(String url, String expectedBody) {
		ResponseEntity<String> response = anon.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}
}
