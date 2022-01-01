package hello.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GreetingTest {

	private Greeting one;

	@BeforeEach
	public void setup() {
		one = new Greeting("Hello, %s!");
		one.setId(1);
	}

	@Test
	public void testGetGreeting() throws Exception {
		assertThat(one.getGreeting("World"), is(equalTo("Hello, World!")));
	}

	@Test
	public void testToString() throws Exception {
		String result = one.toString();
		assertThat(result, containsString("id = 1"));
		assertThat(result, containsString("template = Hello, %s!"));
	}

	@Test
	public void testEquals() throws Exception {
		Greeting two = new Greeting();
		two.setId(one.getId());
		two.setTemplate(one.getTemplate());

		assertThat(one.equals(one), is(true));
		assertThat(one.equals(two), is(true));
		assertThat(two.equals(one), is(true));

		two.setId(2);
		assertThat(one.equals(two), is(false));
		assertThat(two.equals(one), is(false));
	}
}
