package hello.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import hello.assemblers.GreetingModelAssembler;
import hello.dao.GreetingService;
import hello.entities.Greeting;
import hello.exceptions.GreetingNotFoundException;

@RestController
@RequestMapping(value = "/api/greetings", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class GreetingControllerApi {

	@Autowired
	private GreetingService greetingService;

	@Autowired
	private GreetingModelAssembler greetingAssembler;

	@ResponseBody
	@ExceptionHandler(GreetingNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String greetingNotFoundHandler(GreetingNotFoundException ex) {
		return ex.getMessage();
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
}
