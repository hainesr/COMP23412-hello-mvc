package hello.dao;

import java.util.Optional;

import hello.entities.Greeting;

public interface GreetingService {

	public long count();

	public void save(Greeting greeting);

	public Iterable<Greeting> findAll();

	public Optional<Greeting> findById(long id);

	public Greeting findOne(long id);

	public void deleteById(long id);

	public void deleteAll();
}
