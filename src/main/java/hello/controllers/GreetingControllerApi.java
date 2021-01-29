package hello.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@RestController
@RequestMapping(value = "/api/greeting", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class GreetingControllerApi {

	@Autowired
	private GreetingService greetingService;

	@GetMapping
	public CollectionModel<Greeting> list() {
		return greetingToResource(greetingService.findAll());
	}

	@GetMapping("/{id}")
	public EntityModel<Greeting> greeting(@PathVariable("id") long id) {
		Greeting greeting = greetingService.findOne(id);

		return greetingToResource(greeting);
	}

	@GetMapping("/new")
	public ResponseEntity<?> newGreeting() {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createGreeting(@RequestBody @Valid Greeting greeting, BindingResult result) {

		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();
		}

		greetingService.save(greeting);
		URI location = linkTo(GreetingControllerApi.class).slash(greeting.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	private EntityModel<Greeting> greetingToResource(Greeting greeting) {
		Link selfLink = linkTo(GreetingControllerApi.class).slash(greeting.getId()).withSelfRel();

		return EntityModel.of(greeting, selfLink);
	}

	private CollectionModel<Greeting> greetingToResource(Iterable<Greeting> greetings) {
		Link selfLink = linkTo(methodOn(GreetingControllerApi.class).list()).withSelfRel();

		return CollectionModel.of(greetings, selfLink);
	}
}
