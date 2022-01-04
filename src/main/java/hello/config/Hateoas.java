package hello.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.hateoas.server.core.EvoInflectorLinkRelationProvider;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class Hateoas implements RepositoryRestConfigurer {

	private final static Logger log = LoggerFactory.getLogger(Hateoas.class);

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		config.setBasePath("/api");
		log.info("Set REST API base path to '/api'");

		config.setLinkRelationProvider(new EvoInflectorLinkRelationProvider());
		log.info("Using EvoInflectorLinkRelationProvider for HATEOAS relations");
	}
}
