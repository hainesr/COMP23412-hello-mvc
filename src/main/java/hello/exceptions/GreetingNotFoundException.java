package hello.exceptions;

public class GreetingNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GreetingNotFoundException(long id) {
		super("Could not find greeting " + id);
	}
}
