package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

	public static final String ADMIN_ROLE = "ADMINISTRATOR";

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Use form login for the Web, and HTTP basic for the API.
		http.authorizeRequests().anyRequest().permitAll().and().formLogin().loginPage("/sign-in").permitAll().and()
				.logout().logoutUrl("/sign-out").logoutSuccessUrl("/").permitAll().and().httpBasic();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("Rob").password("Haines").roles(ADMIN_ROLE).and().withUser("Caroline")
		.password("Jay").roles(ADMIN_ROLE).and().withUser("Markel").password("Vigo").roles(ADMIN_ROLE);
	}
}
