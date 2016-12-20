package models;

public class Greeting {

	private final String name;
	private final String template = "Hello, %s!";

	public Greeting(String name) {
		this.name = name;
	}

	public String getGreeting() {
		return String.format(template, this.name);
	}

}
