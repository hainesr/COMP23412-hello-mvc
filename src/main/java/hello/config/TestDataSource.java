package hello.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestDataSource {

	private final static Logger log = LoggerFactory.getLogger(TestDataSource.class);

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties) {
		String dbPath = "mem:hello-test";
		String dbOpts = "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

		log.info("Setting H2 path: " + dbPath);
		log.info("Setting H2 options: " + dbOpts);

		return dataSourceProperties.initializeDataSourceBuilder().url("jdbc:h2:" + dbPath + ";" + dbOpts).build();
	}
}
