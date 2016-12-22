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

	private static final String DEFAULT_NAME = "World";

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Greeting> get() {
		Greeting greeting = new Greeting(DEFAULT_NAME);
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/greeting", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Greeting> post(@RequestBody String body) {
		Greeting greeting = new Greeting(getNameFromJson(body, DEFAULT_NAME));
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}

	private String getNameFromJson(String json, String default_name) {
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode rootNode;
		String name = null;

		try {
			rootNode = objectMapper.readTree(json);
			name = rootNode.path("name").textValue();
		} catch (IOException e) {
			// We can ignore this error, as we set a default below
		}

		if (name == null) {
			name = default_name;
		}

		return name;
	}

}
