package hello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.models.Greeting;

@Service
@Transactional
public class GreetingServiceImpl implements GreetingService {

	@Autowired
	private GreetingRepository greetingRepository;

	@Override
	public Iterable<Greeting> findAll() {
		return greetingRepository.findAll();
	}

	@Override
	public Greeting findOne(long id) {
		return greetingRepository.findOne(id);
	}

	@Override
	public void save(Greeting greeting) {
		greetingRepository.save(greeting);
	}

}
