package hello.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@RestController
@RequestMapping(value = "/api/greeting", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class GreetingControllerApi {

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Greeting>> list() {
		return greetingToResource(greetingService.findAll());
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
	public ResponseEntity<?> createGreeting(@RequestBody @Valid Greeting greeting, BindingResult result) {

		if (result.hasErrors()) {
			return ResponseEntity.unprocessableEntity().build();
		}

		greetingService.save(greeting);
		URI location = linkTo(GreetingControllerApi.class).slash(greeting.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	private Resource<Greeting> greetingToResource(Greeting greeting) {
		Link selfLink = linkTo(GreetingControllerApi.class).slash(greeting.getId()).withSelfRel();

		return new Resource<Greeting>(greeting, selfLink);
	}

	private Resources<Resource<Greeting>> greetingToResource(Iterable<Greeting> greetings) {
		Link selfLink = linkTo(methodOn(GreetingControllerApi.class).list()).withSelfRel();

		List<Resource<Greeting>> resources = new ArrayList<Resource<Greeting>>();
		for (Greeting greeting : greetings) {
			resources.add(greetingToResource(greeting));
		}

		return new Resources<Resource<Greeting>>(resources, selfLink);
	}
}
