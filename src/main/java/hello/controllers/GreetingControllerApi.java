package hello.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.assemblers.GreetingModelAssembler;
import hello.dao.GreetingService;
import hello.entities.Greeting;
import hello.exceptions.GreetingNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/greetings", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class GreetingControllerApi {

	private static final String NOT_FOUND_MSG = "{ \"error\": \"%s\", \"id\": \"%d\" }";

	@Autowired
	private GreetingService greetingService;

	@Autowired
	private GreetingModelAssembler greetingAssembler;

	@ExceptionHandler(GreetingNotFoundException.class)
	public ResponseEntity<?> greetingNotFoundHandler(GreetingNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(String.format(NOT_FOUND_MSG, ex.getMessage(), ex.getId()));
	}

	@GetMapping
	public CollectionModel<EntityModel<Greeting>> list() {
		return greetingAssembler.toCollectionModel(greetingService.findAll())
				.add(linkTo(methodOn(GreetingControllerApi.class).list()).withSelfRel());
	}

	@GetMapping("/{id}")
	public EntityModel<Greeting> greeting(@PathVariable("id") long id) {
		Greeting greeting = greetingService.findById(id).orElseThrow(() -> new GreetingNotFoundException(id));

		return greetingAssembler.toModel(greeting);
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

		Greeting newGreeting = greetingService.save(greeting);
		EntityModel<Greeting> entity = greetingAssembler.toModel(newGreeting);

		return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteGreeting(@PathVariable("id") long id) {
		if (!greetingService.existsById(id)) {
			throw new GreetingNotFoundException(id);
		}

		greetingService.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteAllGreetings() {
		greetingService.deleteAll();

		return ResponseEntity.noContent().build();
	}
}
