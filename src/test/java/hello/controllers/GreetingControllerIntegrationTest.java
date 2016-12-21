package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

	@Autowired
	private TestRestTemplate template;

	@Before
	public void setUp() throws MalformedURLException {
		String url = "http://localhost:" + port + "/greeting";
		this.greeting = new URL(url);
		this.greetingName = new URL(url + "?name=Rob");
	}

	@Test
	public void getJsonGreeting() {
		ResponseEntity<String> response = template.getForEntity(greeting.toString(), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
		assertThat(response.getBody(), containsString("Hello, World!"));
	}

	@Test
	public void getJsonGreetingName() {
		ResponseEntity<String> response = template.getForEntity(greetingName.toString(), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));
		assertThat(response.getBody(), containsString("Hello, Rob!"));
	}
}
