package hello.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/*
 * This class provides unit test access to the message converters that are
 * available in production. It is required because they don't get set up
 * automatically when initializing MockMvc in standalone mode.
 *
 * Using a utility class in this way allows us to do this set up once, rather
 * than before every individual unit test.
 */
@Configuration
@Profile("test")
public class MessageConverterUtil implements InitializingBean {

	private final static Logger log = LoggerFactory.getLogger(MessageConverterUtil.class);

	private static HttpMessageConverter<?> converters[];

	@Autowired
	private RequestMappingHandlerAdapter[] adapters;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<HttpMessageConverter<?>> converterList = adapters[0].getMessageConverters();
		converters = new HttpMessageConverter<?>[converterList.size()];
		converterList.toArray(converters);
		log.info("Initializing message converter list for unit testing");
	}

	public static HttpMessageConverter<?>[] getMessageConverters() {
		return converters;
	}
}
