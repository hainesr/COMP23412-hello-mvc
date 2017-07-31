package hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

	public static final String ADMIN_ROLE = "ADMINISTRATOR";

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Set up a matcher that disables CSRF for the GET, HEAD, TRACE and
		// OPTIONS methods and application/json requests.
		ContentNegotiationStrategy negotiationStrategy = new HeaderContentNegotiationStrategy();
		RequestMatcher jsonMatcher = new MediaTypeRequestMatcher(negotiationStrategy, MediaType.APPLICATION_JSON);
		RequestMatcher csrfMatcher = new AndRequestMatcher(CsrfFilter.DEFAULT_CSRF_MATCHER,
				new NegatedRequestMatcher(jsonMatcher));

		// Allow all requests and we use method security in the controllers.
		http.authorizeRequests().anyRequest().permitAll();

		// Use form login/logout for the Web.
		http.formLogin().loginPage("/sign-in").permitAll();
		http.logout().logoutUrl("/sign-out").logoutSuccessUrl("/").permitAll();

		// Use HTTP basic for the API.
		http.httpBasic();

		// Only use CSRF for Web requests.
		http.csrf().requireCsrfProtectionMatcher(csrfMatcher);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("Rob").password("Haines").roles(ADMIN_ROLE);
		auth.inMemoryAuthentication().withUser("Caroline").password("Jay").roles(ADMIN_ROLE);
		auth.inMemoryAuthentication().withUser("Markel").password("Vigo").roles(ADMIN_ROLE);
	}
}
