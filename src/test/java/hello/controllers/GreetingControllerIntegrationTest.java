package hello.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hello.Hello;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class GreetingControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	@LocalServerPort
	private int port;

	private String baseUrl;
	private String loginUrl;
	private String greetingUrl;
	private String greetingNameUrl;

	private static final String INDEX = "/1";

	private HttpEntity<String> httpEntity;

	// We need cookies for Web log in.
	// Initialize this each time we need it to ensure it's clean.
	private TestRestTemplate stateful;

	// An anonymous and stateless log in.
	private final TestRestTemplate anon = new TestRestTemplate();

	@Before
	public void setUp() throws MalformedURLException {
		this.baseUrl = "http://localhost:" + port + "/greeting";
		this.loginUrl = "http://localhost:" + port + "/sign-in";
		this.greetingUrl = baseUrl + INDEX;
		this.greetingNameUrl = baseUrl + INDEX + "?name=Rob";

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void getGreeting() {
		get(greetingUrl, "Hello, World!");
	}

	@Test
	public void getGreetingName() {
		get(greetingNameUrl, "Hello, Rob!");
	}

	@Test
	public void getLoginForm() {
		get(loginUrl, "_csrf");
	}

	@Test
	public void testLogin() {
		stateful = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		HttpEntity<String> formEntity = new HttpEntity<>(headers);
		ResponseEntity<String> formResponse = stateful.exchange(loginUrl, HttpMethod.GET, formEntity, String.class);
		String csrfToken = getCsrfToken(formResponse.getBody());
		String cookie = formResponse.getHeaders().getFirst("Set-Cookie");

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Cookie", cookie);

		MultiValueMap<String, String> login = new LinkedMultiValueMap<>();
		login.add("_csrf", csrfToken);
		login.add("username", "Markel");
		login.add("password", "Vigo");

		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(login,
				headers);
		ResponseEntity<String> loginResponse = stateful.exchange(loginUrl, HttpMethod.POST, postEntity, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat(loginResponse.getHeaders().getLocation().toString(), endsWith(":" + this.port + "/"));
	}

	@Test
	public void testBadUserLogin() {
		stateful = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		HttpEntity<String> formEntity = new HttpEntity<>(headers);
		ResponseEntity<String> formResponse = stateful.exchange(loginUrl, HttpMethod.GET, formEntity, String.class);
		String csrfToken = getCsrfToken(formResponse.getBody());
		String cookie = formResponse.getHeaders().getFirst("Set-Cookie");

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Cookie", cookie);

		MultiValueMap<String, String> login = new LinkedMultiValueMap<>();
		login.add("_csrf", csrfToken);
		login.add("username", "Robert");
		login.add("password", "Haines");

		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(login,
				headers);
		ResponseEntity<String> loginResponse = stateful.exchange(loginUrl, HttpMethod.POST, postEntity, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat(loginResponse.getHeaders().getLocation().toString(), endsWith("/sign-in?error"));
	}

	@Test
	public void testBadPasswordLogin() {
		stateful = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		HttpEntity<String> formEntity = new HttpEntity<>(headers);
		ResponseEntity<String> formResponse = stateful.exchange(loginUrl, HttpMethod.GET, formEntity, String.class);
		String csrfToken = getCsrfToken(formResponse.getBody());
		String cookie = formResponse.getHeaders().getFirst("Set-Cookie");

		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Cookie", cookie);

		MultiValueMap<String, String> login = new LinkedMultiValueMap<>();
		login.add("_csrf", csrfToken);
		login.add("username", "Caroline");
		login.add("password", "J");

		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(login,
				headers);
		ResponseEntity<String> loginResponse = stateful.exchange(loginUrl, HttpMethod.POST, postEntity, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat(loginResponse.getHeaders().getLocation().toString(), endsWith("/sign-in?error"));
	}

	@Test
	public void postGreetingNoLogin() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("template", "Howdy, %s!");
		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(form,
				postHeaders);

		ResponseEntity<String> response = anon.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
		assertThat(2, equalTo(countRowsInTable("greeting")));
	}

	@Test
	@DirtiesContext
	public void postGreetingWithLogin() {
		stateful = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);

		// Set up headers for GETting and POSTing.
		HttpHeaders getHeaders = new HttpHeaders();
		getHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// GET the log in page so we can read the CSRF token and the session
		// cookie.
		HttpEntity<String> getEntity = new HttpEntity<>(getHeaders);
		ResponseEntity<String> formResponse = stateful.exchange(loginUrl, HttpMethod.GET, getEntity, String.class);
		String csrfToken = getCsrfToken(formResponse.getBody());
		String cookie = formResponse.getHeaders().getFirst("Set-Cookie").split(";")[0];

		// Set the session cookie and populate the log in form.
		postHeaders.set("Cookie", cookie);
		MultiValueMap<String, String> login = new LinkedMultiValueMap<>();
		login.add("_csrf", csrfToken);
		login.add("username", "Rob");
		login.add("password", "Haines");

		// Log in.
		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(login,
				postHeaders);
		ResponseEntity<String> loginResponse = stateful.exchange(loginUrl, HttpMethod.POST, postEntity, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));

		// Set the session cookie and GET the new greeting form so we can read
		// the new CSRF token.
		getHeaders.set("Cookie", cookie);
		getEntity = new HttpEntity<>(getHeaders);
		formResponse = stateful.exchange(loginUrl, HttpMethod.GET, getEntity, String.class);
		csrfToken = getCsrfToken(formResponse.getBody());

		// Populate the new greeting form.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", csrfToken);
		form.add("template", "Howdy, %s!");
		postEntity = new HttpEntity<MultiValueMap<String, String>>(form, postHeaders);

		// POST the new greeting.
		ResponseEntity<String> response = stateful.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		// Did it work?
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().toString(), containsString(baseUrl));
		assertThat(3, equalTo(countRowsInTable("greeting")));
	}

	private void get(String url, String expectedBody) {
		ResponseEntity<String> response = anon.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}

	private String getCsrfToken(String body) {
		Pattern pattern = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		Matcher matcher = pattern.matcher(body);

		// matcher.matches() must be called!
		assertThat(matcher.matches(), equalTo(true));

		return matcher.group(1);
	}
}
