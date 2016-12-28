package hello.services;

import hello.models.Greeting;

public interface GreetingService {

	public void save(Greeting greeting);

	public Iterable<Greeting> findAll();

	public Greeting findOne(long id);

}
