package hello.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@RestController
@RequestMapping(value = "/greeting", produces = MediaType.APPLICATION_JSON_VALUE)
public class GreetingControllerRest {

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Iterable<Greeting>> list() {
		return new ResponseEntity<Iterable<Greeting>>(greetingService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resource<Greeting> greeting(@PathVariable("id") long id) {
		Greeting greeting = greetingService.findOne(id);

		return greetingToResource(greeting);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ResponseEntity<?> newGreeting() {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createGreeting(@RequestBody @Valid Greeting greeting,
			BindingResult result, UriComponentsBuilder b) {

		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();
		}

		greetingService.save(greeting);
		UriComponents location = b.path("/greeting/{id}").buildAndExpand(greeting.getId());

		return ResponseEntity.created(location.toUri()).build();
	}

	private Resource<Greeting> greetingToResource(Greeting greeting) {
		Link selfLink = linkTo(GreetingControllerRest.class).slash(greeting.getId()).withSelfRel();

		return new Resource<Greeting>(greeting, selfLink);
	}
}
