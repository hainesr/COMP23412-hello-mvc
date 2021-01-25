package hello.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ContentNegotiation implements WebMvcConfigurer {

	// Although deprecated we need to continue to use `favorPathExtension(false)`
	// until Spring 5.3.
	@SuppressWarnings("deprecation")
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false).favorParameter(false).ignoreAcceptHeader(false)
				.defaultContentType(MediaType.TEXT_HTML).mediaType("html", MediaType.TEXT_HTML)
				.mediaType("json", MediaType.APPLICATION_JSON);
	}
}
