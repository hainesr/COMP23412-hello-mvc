package hello.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class Security {

	public static final String ADMIN_ROLE = "ADMINISTRATOR";

	// List the mappings/methods for which no authorisation is required.
	// This includes the paths where static resources, such as bootstrap, are
	// located. We also specifically omit '/greeting/new' here so that we require
	// log in before submitting the new greeting.
	private static final RequestMatcher[] NO_AUTH = { antMatcher(HttpMethod.GET, "/webjars/**"),
			antMatcher(HttpMethod.GET, "/"), antMatcher(HttpMethod.GET, "/api/**"),
			antMatcher(HttpMethod.GET, "/greetings"), antMatcher(HttpMethod.GET, "/greetings/{id:[\\d]+}") };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// By default, all requests are authenticated except our specific list.
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(NO_AUTH).permitAll().anyRequest().hasRole(ADMIN_ROLE))

				// This makes testing easier. Given we're not going into production, that's OK.
				.sessionManagement(session -> session.requireExplicitAuthenticationStrategy(false))

				// Use form login/logout for the Web.
				.formLogin(login -> login.loginPage("/sign-in").permitAll())
				.logout(logout -> logout.logoutUrl("/sign-out").logoutSuccessUrl("/").permitAll())

				// Use HTTP basic for the API.
				.httpBasic(withDefaults()).securityMatcher(antMatcher("/api/**"))

				// Only use CSRF for Web requests.
				.csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher("/api/**"))).securityMatcher(antMatcher("/**"));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		UserDetails rob = User.withUsername("Rob").password(encoder.encode("Haines")).roles(ADMIN_ROLE).build();
		UserDetails caroline = User.withUsername("Caroline").password(encoder.encode("Jay")).roles(ADMIN_ROLE).build();
		UserDetails markel = User.withUsername("Markel").password(encoder.encode("Vigo")).roles(ADMIN_ROLE).build();
		UserDetails mustafa = User.withUsername("Mustafa").password(encoder.encode("Mustafa")).roles(ADMIN_ROLE)
				.build();
		UserDetails tom = User.withUsername("Tom").password(encoder.encode("Carroll")).roles(ADMIN_ROLE).build();

		return new InMemoryUserDetailsManager(rob, caroline, markel, mustafa, tom);
	}
}
