package hello.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import hello.controllers.GreetingControllerApi;
import hello.entities.Greeting;

@Component
public class GreetingModelAssembler implements RepresentationModelAssembler<Greeting, EntityModel<Greeting>> {

	@Override
	public EntityModel<Greeting> toModel(Greeting greeting) {
		return EntityModel.of(greeting,
				linkTo(methodOn(GreetingControllerApi.class).greeting(greeting.getId())).withSelfRel(),
				linkTo(methodOn(GreetingControllerApi.class).list()).withRel("greetings"));
	}
}
