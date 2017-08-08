package hello.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("test")
public class TestPersistence {

	private final static Logger log = LoggerFactory.getLogger(TestPersistence.class);

	// Persistence objects.
	private final static String PACKAGES = "hello.entities";

	// Connection properties.
	private final static String DB_PATH = "mem:hello-test";
	private final static String DB_OPTS = "DB_CLOSE_DELAY=-1";

	// Hibernate properties.
	private final static boolean H2_SHOW_SQL = true;
	private final static String H2_HBM2DDL_AUTO = "create-drop";
	private final static String H2_USERNAME = "h2";
	private final static String H2_PASSWORD = "spring";

	@Bean
	public DataSource dataSource() {
		String dbUrl = "jdbc:h2:" + DB_PATH + ";" + DB_OPTS;

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl(dbUrl);
		dataSource.setUsername(H2_USERNAME);
		dataSource.setPassword(H2_PASSWORD);

		log.info("Database URL set: " + dbUrl);

		return dataSource;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource());
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		bean.setPackagesToScan(PACKAGES);
		bean.setJpaProperties(hibernateProperties());
		bean.afterPropertiesSet();

		return bean.getObject();
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.H2);
		adapter.setShowSql(H2_SHOW_SQL);
		adapter.setGenerateDdl(true);

		return adapter;
	}

	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", H2_HBM2DDL_AUTO);

		return properties;
	}
}
