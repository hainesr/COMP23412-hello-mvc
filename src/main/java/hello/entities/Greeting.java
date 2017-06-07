package hello.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.hateoas.core.Relation;

@Entity
@Relation(collectionRelation = "greetings")
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
