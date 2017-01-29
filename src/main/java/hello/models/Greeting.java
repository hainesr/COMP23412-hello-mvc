package hello.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Greeting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Size(min = 1, max = 30)
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
}
