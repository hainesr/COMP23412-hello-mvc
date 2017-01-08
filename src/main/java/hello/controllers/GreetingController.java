package hello.controllers;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import hello.models.Greeting;
import hello.services.GreetingService;

@Controller
@RequestMapping(value = "/greeting")
public class GreetingController {

	private static final String[] NAMES = { "Rob", "Caroline", "Markel" };

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public String list(Model model) {

		model.addAttribute("greetings", greetingService.findAll());
		model.addAttribute("names", NAMES);

		return "greeting/index";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public String greeting(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Greeting greeting = greetingService.findOne(id);
		model.addAttribute("greeting", greeting.getGreeting(name));

		return "greeting/show";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String newGreeting(Model model) {
		return "greeting/new";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
			MediaType.TEXT_HTML_VALUE })
	public String createGreetingFromForm(@RequestBody String form, Model model) {

		try {
			Greeting greeting = Greeting.fromUriParameters(form);
			greetingService.save(greeting);
		} catch (UnsupportedEncodingException e) {
			// Go back to the new greeting page
			return "greeting/new";
		}

		return list(model);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> createGreetingFromJson(@RequestBody Greeting greeting,
			UriComponentsBuilder b) {

		greetingService.save(greeting);
		UriComponents location = b.path("/greeting/{id}").buildAndExpand(greeting.getId());

		return ResponseEntity.created(location.toUri()).build();
	}

}
