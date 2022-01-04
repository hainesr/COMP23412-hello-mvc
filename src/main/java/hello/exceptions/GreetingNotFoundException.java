package hello.exceptions;

public class GreetingNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private long id;

	public GreetingNotFoundException(long id) {
		super("Could not find greeting " + id);

		this.id = id;
	}

	public long getId() {
		return id;
	}
}
