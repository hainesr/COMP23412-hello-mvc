package hello.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import models.Greeting;

@Controller
@RequestMapping("/greeting")
public class GreetingController {

	@RequestMapping(method = RequestMethod.GET, produces = { "application/json" })
	public @ResponseBody Greeting getGreeting(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		return new Greeting(name);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("greeting", getGreeting(name));

		return "greeting";
	}

}
