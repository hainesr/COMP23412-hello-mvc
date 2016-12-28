package hello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import hello.models.Greeting;
import hello.services.GreetingService;

@RestController
@RequestMapping(value = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ApiController {

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Iterable<Greeting>> getAll() {
		Iterable<Greeting> all = greetingService.findAll();

		return ResponseEntity.ok(all);
	}

	@RequestMapping(value = "/greeting/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Greeting> get(@PathVariable("id") long id) {

		Greeting greeting = greetingService.findOne(id);

		return ResponseEntity.ok(greeting);
	}

	@RequestMapping(value = "/greeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> post(@RequestBody Greeting greeting, UriComponentsBuilder b) {

		greetingService.save(greeting);

		UriComponents location = b.path("/api/greeting/{id}").buildAndExpand(greeting.getId());

		return ResponseEntity.created(location.toUri()).build();
	}

}
