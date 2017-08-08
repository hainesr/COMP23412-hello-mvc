package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import hello.Hello;
import hello.dao.GreetingService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GreetingControllerApiIntegrationTest {

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

	@Autowired
	private GreetingService greetingService;

	@Before
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

		long before = greetingService.count();
		ResponseEntity<String> response = anon.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);
		long after = greetingService.count();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
		assertThat(before, equalTo(after));
	}

	@Test
	public void postGreetingBadAuth() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		long before = greetingService.count();
		ResponseEntity<String> response = evil.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);
		long after = greetingService.count();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
		assertThat(before, equalTo(after));
	}

	@Test
	@DirtiesContext
	public void postGreeting() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		long before = greetingService.count();
		ResponseEntity<String> response = user.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);
		long after = greetingService.count();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
		assertThat(response.getHeaders().getLocation().toString(), containsString(baseUrl));
		assertThat(response.getBody(), equalTo(null));
		assertThat((before + 1), equalTo(after));
	}

	private void get(String url, String expectedBody) {
		ResponseEntity<String> response = anon.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}
}
