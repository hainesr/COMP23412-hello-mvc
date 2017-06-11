package hello.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.EvoInflectorRelProvider;

@Configuration
public class Hateoas {

	private final static Logger log = LoggerFactory.getLogger(Hateoas.class);

	@Bean
	public RelProvider relProvider() {
		log.info("Using EvoInflectorRelProvider for HATEOAS relations");

		return new EvoInflectorRelProvider();
	}
}
