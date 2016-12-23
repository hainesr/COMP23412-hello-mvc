package hello.models;

public class Greeting {

	private static final String DEFAULT_NAME = "World";

	private final String name;
	private final String template = "Hello, %s!";

	public Greeting() {
		this(DEFAULT_NAME);
	}

	public Greeting(String name) {
		name = (name == null ? DEFAULT_NAME : name);

		this.name = name;
	}

	public String getGreeting() {
		return String.format(template, this.name);
	}

}
