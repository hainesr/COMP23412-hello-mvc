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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import hello.Hello;
import hello.services.GreetingService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class GreetingControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private String baseUrl;
	private String greetingUrl;
	private String greetingNameUrl;

	private static final String INDEX = "/1";

	private HttpEntity<String> htmlEntity;
	private HttpEntity<String> jsonEntity;

	@Autowired
	private TestRestTemplate template;

	@Autowired
	private GreetingService greetingService;

	@Before
	public void setUp() throws MalformedURLException {
		this.baseUrl = "http://localhost:" + port + "/greeting";
		this.greetingUrl = baseUrl + INDEX;
		this.greetingNameUrl = baseUrl + INDEX + "?name=Rob";

		HttpHeaders htmlHeaders = new HttpHeaders();
		htmlHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		htmlEntity = new HttpEntity<String>(htmlHeaders);

		HttpHeaders jsonHeaders = new HttpHeaders();
		jsonHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		jsonEntity = new HttpEntity<String>(jsonHeaders);
	}

	@Test
	public void getHtmlGreeting() {
		getHtml(greetingUrl, "Hello, World!");
	}

	@Test
	public void getJsonGreeting() {
		getJson(greetingUrl, "Hello, World!");
	}

	@Test
	public void getHtmlGreetingName() {
		getHtml(greetingNameUrl, "Hello, Rob!");
	}

	@Test
	public void getJsonGreetingName() {
		getJson(greetingNameUrl, "Hello, Rob!");
	}

	@Test
	public void postHtmlGreeting() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<String> postEntity = new HttpEntity<String>("template=Howdy%2C%20%25s!", postHeaders);

		long before = greetingService.count();
		ResponseEntity<String> response = template.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);
		long after = greetingService.count();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat((before + 1), equalTo(after));
	}

	@Test
	public void postJsonGreeting() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"template\": \"Howdy, %s!\" }", postHeaders);

		long before = greetingService.count();
		ResponseEntity<String> response = template.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);
		long after = greetingService.count();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
		assertThat(response.getHeaders().getLocation().toString(), containsString(baseUrl));
		assertThat(response.getBody(), equalTo(null));
		assertThat((before + 1), equalTo(after));
	}

	private void getHtml(String url, String expectedBody) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, htmlEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}

	private void getJson(String url, String expectedBody) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, jsonEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}
}
