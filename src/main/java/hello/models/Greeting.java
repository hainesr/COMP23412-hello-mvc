package hello.models;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Entity
public class Greeting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String template;

	public Greeting() {
	}

	public Greeting(String template) {
		this.template = template;
	}

	public long getId() {
		return this.id;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getGreeting(String name) {
		return String.format(this.template, name);
	}

	@Override
	public String toString() {
		return String.format("Greeting [id = %d, template = %s]", this.id, this.template);
	}

	public static Greeting fromUriParameters(String parameters) throws UnsupportedEncodingException {

		// Need to fake a parameter string with "?" at the start.
		UriComponents uri = UriComponentsBuilder.fromUriString("?" + parameters).build();
		MultiValueMap<String, String> fields = uri.getQueryParams();
		String template = URLDecoder.decode(fields.getFirst("template"), "UTF-8");

		return new Greeting(template);
	}

}
