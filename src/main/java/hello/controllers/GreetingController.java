package hello.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import models.Greeting;

@Controller
@RequestMapping(value = "/greeting", produces = { MediaType.TEXT_HTML_VALUE })
public class GreetingController {

	@RequestMapping(method = RequestMethod.GET)
	public String greeting(@RequestParam(value = "name", required = false) String name, Model model) {
		model.addAttribute("greeting", new Greeting(name));

		return "greeting";
	}

}
