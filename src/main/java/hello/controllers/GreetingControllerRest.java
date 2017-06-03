package hello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
