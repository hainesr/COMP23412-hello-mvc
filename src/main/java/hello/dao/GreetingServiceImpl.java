package hello.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.entities.Greeting;

@Service
@Transactional
public class GreetingServiceImpl implements GreetingService {

	@Autowired
	private GreetingRepository greetingRepository;

	@Override
	public long count() {
		return greetingRepository.count();
	}

	@Override
	public boolean existsById(long id) {
		return greetingRepository.existsById(id);
	}

	@Override
	public Greeting save(Greeting greeting) {
		return greetingRepository.save(greeting);
	}

	@Override
	public Iterable<Greeting> saveAll(Iterable<Greeting> greetings) {
		return greetingRepository.saveAll(greetings);
	}

	@Override
	public Iterable<Greeting> findAll() {
		return greetingRepository.findAll();
	}

	@Override
	public Optional<Greeting> findById(long id) {
		return greetingRepository.findById(id);
	}

	@Override
	public Iterable<Greeting> findAllById(Iterable<Long> ids) {
		return greetingRepository.findAllById(ids);
	}

	@Override
	public void delete(Greeting greeting) {
		greetingRepository.delete(greeting);
	}

	@Override
	public void deleteById(long id) {
		greetingRepository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		greetingRepository.deleteAll();
	}

	@Override
	public void deleteAll(Iterable<Greeting> greetings) {
		greetingRepository.deleteAll(greetings);
	}

	@Override
	public void deleteAllById(Iterable<Long> ids) {
		greetingRepository.deleteAllById(ids);
	}
}
