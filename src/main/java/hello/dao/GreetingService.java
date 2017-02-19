package hello.dao;

import hello.entities.Greeting;

public interface GreetingService {

	public long count();

	public void save(Greeting greeting);

	public Iterable<Greeting> findAll();

	public Greeting findOne(long id);

	public void delete(long id);

	public void deleteAll();
}
