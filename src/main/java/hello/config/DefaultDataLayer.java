package hello.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@Profile("default")
public class DefaultDataLayer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean
	public DataSource dataSource(DataSourceProperties dataSourceProperties) {
		Path dbPath = Paths.get(System.getProperty("user.dir"), "db", "data");
		String dbOpts = "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

		log.info("Setting H2 path: " + dbPath);
		log.info("Setting H2 options: " + dbOpts);

		return dataSourceProperties.initializeDataSourceBuilder().url("jdbc:h2:" + dbPath + ";" + dbOpts).build();
	}

	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setJpaVendorAdapter(jpaVendorAdapter);
		bean.setPackagesToScan("hello");
		bean.afterPropertiesSet();

		return bean.getObject();
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.H2);
		adapter.setShowSql(false);
		adapter.setGenerateDdl(true);

		return adapter;
	}

	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

}
