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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

import hello.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:db/greetings-init.sql")
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
		ResponseEntity<String> response = template.exchange(greetingUrl, HttpMethod.GET, htmlEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString("Hello, World!"));
	}

	@Test
	public void getJsonGreeting() {
		ResponseEntity<String> response = template.exchange(greetingUrl, HttpMethod.GET, jsonEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
		assertThat(response.getBody(), containsString("Hello, World!"));
	}

	@Test
	public void getHtmlGreetingName() {
		ResponseEntity<String> response = template.exchange(greetingNameUrl, HttpMethod.GET, htmlEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString("Hello, Rob!"));
	}

	@Test
	public void getJsonGreetingName() {
		ResponseEntity<String> response = template.exchange(greetingNameUrl, HttpMethod.GET, jsonEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
		assertThat(response.getBody(), containsString("Hello, Rob!"));
	}
}
