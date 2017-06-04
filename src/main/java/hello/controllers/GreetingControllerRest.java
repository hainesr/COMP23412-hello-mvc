package hello.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/greeting")
public class GreetingControllerRest {

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Iterable<Greeting>> list() {
		return new ResponseEntity<Iterable<Greeting>>(greetingService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Greeting> greeting(@PathVariable("id") long id) {
		return new ResponseEntity<Greeting>(greetingService.findOne(id), HttpStatus.OK);
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
}
