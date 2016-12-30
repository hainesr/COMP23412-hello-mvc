package hello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import hello.models.Greeting;
import hello.services.GreetingService;

@Controller
@RequestMapping(value = "/greeting", produces = { MediaType.TEXT_HTML_VALUE })
public class GreetingController {

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String greeting(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Greeting greeting = greetingService.findOne(id);
		model.addAttribute("greeting", greeting.getGreeting(name));

		return "greeting/show";
	}

}
