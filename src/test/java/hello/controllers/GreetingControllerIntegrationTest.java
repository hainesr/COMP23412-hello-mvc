package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.springframework.test.context.junit4.SpringRunner;

import hello.configuration.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingControllerIntegrationTest {

	@LocalServerPort
	private int port;

	private URL greeting;
	private URL greetingName;

	private HttpEntity<String> entity;

	@Autowired
	private TestRestTemplate template;

	@Before
	public void setUp() throws MalformedURLException {
		String url = "http://localhost:" + port + "/greeting";
		this.greeting = new URL(url);
		this.greetingName = new URL(url + "?name=Rob");

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		entity = new HttpEntity<String>("parameters", headers);
	}

	@Test
	public void getHtmlGreeting() {
		ResponseEntity<String> response = template.exchange(greeting.toString(), HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString("Hello, World!"));
	}

	@Test
	public void getJsonGreeting() {
		ResponseEntity<String> response = template.getForEntity(greeting.toString(), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
		assertThat(response.getBody(), containsString("Hello, World!"));
	}

	@Test
	public void getHtmlGreetingName() {
		ResponseEntity<String> response = template.exchange(greetingName.toString(), HttpMethod.GET, entity,
				String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString("Hello, Rob!"));
	}

	@Test
	public void getJsonGreetingName() {
		ResponseEntity<String> response = template.getForEntity(greetingName.toString(), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
		assertThat(response.getBody(), containsString("Hello, Rob!"));
	}
}
