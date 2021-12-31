package hello.config.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@Configuration
@Profile("test")
public class TestDataLoader {
	private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

	private final static String[] TEMPLATES = { "Hello, %s!", "Howdy, %s!" };

	@Bean
	CommandLineRunner initDatabase(GreetingService greetingService) {
		return args -> {
			for (String template : TEMPLATES) {
				log.info("Preloading: " + greetingService.save(new Greeting(template)));
			}
		};
	}
}
