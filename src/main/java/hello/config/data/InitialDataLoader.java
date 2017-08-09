package hello.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	private final static String[] TEMPLATES = { "Hello, %s!", "Howdy, %s!" };

	@Autowired
	private GreetingService greetingService;


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (greetingService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}

		for (String template : TEMPLATES) {
			Greeting greeting = new Greeting();
			greeting.setTemplate(template);

			greetingService.save(greeting);
			log.info("Added greeting (" + greeting.getId() + "): " + greeting.getTemplate());
		}
	}
}
