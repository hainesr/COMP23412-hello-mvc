package hello.dao;

import java.util.Optional;

import hello.entities.Greeting;

public interface GreetingService {

	public long count();

	public boolean existsById(long id);

	public Greeting save(Greeting greeting);

	public Iterable<Greeting> saveAll(Iterable<Greeting> greetings);

	public Iterable<Greeting> findAll();

	public Optional<Greeting> findById(long id);

	public Iterable<Greeting> findAllById(Iterable<Long> ids);

	public void delete(Greeting greeting);

	public void deleteById(long id);

	public void deleteAll();

	public void deleteAll(Iterable<Greeting> greetings);

	public void deleteAllById(Iterable<Long> ids);
}
