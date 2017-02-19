package hello.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import hello.models.Greeting;
import hello.services.GreetingService;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	private final static String GREETING = "Hello, %s!";

	@Autowired
	private GreetingService greetingService;


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (greetingService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}

		Greeting greeting = new Greeting();
		greeting.setTemplate(GREETING);

		greetingService.save(greeting);

		log.info("Added greeting (" + greeting.getId() + "): " + greeting.getTemplate());
	}
}
