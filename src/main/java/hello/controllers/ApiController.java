package hello.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Greeting;

@RestController
@RequestMapping(value = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
public class ApiController {

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Greeting> get() {
		Greeting greeting = new Greeting("World");
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/greeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Greeting> post(@RequestBody String body) {
		Greeting greeting = new Greeting(getNameFromJson(body));
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}
	
	private String getNameFromJson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode;
		String name;
		
		try {
			rootNode = objectMapper.readTree(json);
			name = rootNode.path("name").textValue();
		} catch (IOException e) {
			name = "World";
		}
		
		return name;
	}

}
