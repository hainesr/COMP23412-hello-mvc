package hello.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class Container implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	private final static Logger log = LoggerFactory.getLogger(Container.class);
	private final static String PORT_ENV = "HELLO_MVC_PORT";
	private static int DEFAULT_PORT = 8080;

	@Override
	public void customize(ConfigurableServletWebServerFactory factory) {
		int port = DEFAULT_PORT;

		try {
			port = Integer.parseInt(System.getenv(PORT_ENV));
			log.info("Using port number from " + PORT_ENV + ": " + port);
		} catch (NumberFormatException nfe) {
			log.info("Using default port number: " + DEFAULT_PORT);
		}

		factory.setPort(port);
	}
}
