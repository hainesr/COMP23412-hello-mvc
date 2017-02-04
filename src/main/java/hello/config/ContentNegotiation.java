package hello.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsProperties;

@Configuration
public class ContentNegotiation extends WebMvcConfigurerAdapter {

	@Autowired
	private HandlebarsProperties handlebars;

	@Autowired
	private Environment environment;

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false).favorParameter(false).ignoreAcceptHeader(false).useJaf(false)
		.defaultContentType(MediaType.TEXT_HTML).mediaType("html", MediaType.TEXT_HTML)
		.mediaType("json", MediaType.APPLICATION_JSON);
	}

	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {

		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
		resolvers.add(getHtmlTemplateViewResolver());
		resolvers.add(getJsonTemplateViewResolver());
		resolvers.add(new JsonViewResolver());

		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);
		resolver.setViewResolvers(resolvers);

		return resolver;
	}

	private ViewResolver getHtmlTemplateViewResolver() {
		HandlebarsViewResolver resolver = new HandlebarsViewResolver();
		handlebars.applyToViewResolver(resolver);
		resolver.setSuffix(".html.hbs");
		resolver.setContentType(MediaType.TEXT_HTML_VALUE);
		resolver.setCache(!isDefaultProfile());
		resolver.setOrder(1);

		return resolver;
	}

	private ViewResolver getJsonTemplateViewResolver() {
		HandlebarsViewResolver resolver = new HandlebarsViewResolver();
		handlebars.applyToViewResolver(resolver);
		resolver.setSuffix(".json.hbs");
		resolver.setContentType(MediaType.APPLICATION_JSON_VALUE);
		resolver.setCache(!isDefaultProfile());
		resolver.setOrder(2);

		return resolver;
	}

	private boolean isDefaultProfile() {
		String[] profiles = environment.getActiveProfiles();

		return (profiles.length == 0) || Arrays.asList(environment.getActiveProfiles()).contains("default");
	}
}
