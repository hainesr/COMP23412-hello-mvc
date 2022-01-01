package hello.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Greeting {

	@Id
	@GeneratedValue
	private long id;

	@NotEmpty(message = "The greeting cannot be empty.")
	@Size(max = 30, message = "The greeting must have 30 characters or less.")
	@Pattern(regexp = ".*%s.*", message = "The greeting must include the %s placeholder.")
	private String template;

	public Greeting() {
	}

	public Greeting(String template) {
		this.template = template;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
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

	@Override
	public boolean equals(Object o) {
		return Objects.equals(this.hashCode(), o.hashCode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.template);
	}
}
