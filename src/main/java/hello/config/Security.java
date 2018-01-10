package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

	public static final String ADMIN_ROLE = "ADMINISTRATOR";

	// List the mappings/methods for which no authorisation is required.
	// We specifically omit '/greeting/new' here so that we require log in
	// before submitting the new greeting.
	private static final RequestMatcher[] NO_AUTH = { new AntPathRequestMatcher("/", "GET"),
			new AntPathRequestMatcher("/api/**", "GET"), new AntPathRequestMatcher("/greeting", "GET"),
			new AntPathRequestMatcher("/greeting/{id:[\\d]+}", "GET"), new AntPathRequestMatcher("/**", "DELETE") };

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// By default, all requests are authenticated except our specific list.
		http.authorizeRequests().requestMatchers(NO_AUTH).permitAll().anyRequest().hasRole(ADMIN_ROLE);

		// Use form login/logout for the Web.
		http.formLogin().loginPage("/sign-in").permitAll();
		http.logout().logoutUrl("/sign-out").logoutSuccessUrl("/").permitAll();

		// Use HTTP basic for the API.
		http.requestMatcher(new AntPathRequestMatcher("/api/**")).httpBasic();

		// Only use CSRF for Web requests.
		http.antMatcher("/**").csrf().ignoringAntMatchers("/api/**");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("Rob").password("Haines").roles(ADMIN_ROLE);
		auth.inMemoryAuthentication().withUser("Caroline").password("Jay").roles(ADMIN_ROLE);
		auth.inMemoryAuthentication().withUser("Markel").password("Vigo").roles(ADMIN_ROLE);
	}
}
