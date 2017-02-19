package hello.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class DefaultDataSource {

	private final static Logger log = LoggerFactory.getLogger(DefaultDataSource.class);

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties) {
		Path dbPath = Paths.get(System.getProperty("user.dir"), "db", "hello-dev");
		String dbOpts = "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

		log.info("Setting H2 path: " + dbPath);
		log.info("Setting H2 options: " + dbOpts);

		return dataSourceProperties.initializeDataSourceBuilder().url("jdbc:h2:" + dbPath + ";" + dbOpts).build();
	}
}
