package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

	private static final String ADMIN_ROLE = "ADMINISTRATOR";

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").permitAll().anyRequest().hasRole(ADMIN_ROLE).and().formLogin()
		.loginPage("/sign-in").permitAll().and().logout().logoutUrl("/sign-out").logoutSuccessUrl("/")
		.permitAll();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("Rob").password("Haines").roles(ADMIN_ROLE).and().withUser("Caroline")
				.password("Jay").roles(ADMIN_ROLE).and().withUser("Markel").password("Vigo").roles(ADMIN_ROLE);
	}
}
