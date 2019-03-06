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
	public Iterable<Greeting> findAll() {
		return greetingRepository.findAll();
	}

	@Override
	public Optional<Greeting> findById(long id) {
		return greetingRepository.findById(id);
	}

	@Override
	public Greeting findOne(long id) {
		return findById(id).orElse(null);
	}

	@Override
	public void save(Greeting greeting) {
		greetingRepository.save(greeting);
	}

	@Override
	public void deleteById(long id) {
		greetingRepository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		greetingRepository.deleteAll();
	}
}
