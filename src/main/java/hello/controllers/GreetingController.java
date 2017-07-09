package hello.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import hello.config.Security;
import hello.dao.GreetingService;
import hello.entities.Greeting;

@Controller
@RequestMapping(value = "/greeting", produces = MediaType.TEXT_HTML_VALUE)
public class GreetingController {

	private static final String AUTH_ROLE = "hasRole('" + Security.ADMIN_ROLE + "')";
	private static final String[] NAMES = { "Rob", "Caroline", "Markel" };

	@Autowired
	private GreetingService greetingService;

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) {

		model.addAttribute("greetings", greetingService.findAll());
		model.addAttribute("names", NAMES);

		return "greeting/index";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String greeting(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Greeting greeting = greetingService.findOne(id);
		model.addAttribute("greeting", greeting.getGreeting(name));

		return "greeting/show";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	@PreAuthorize(AUTH_ROLE)
	public String newGreeting(Model model) {
		if (!model.containsAttribute("greeting")) {
			model.addAttribute("greeting", new Greeting());
		}

		return "greeting/new";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@PreAuthorize(AUTH_ROLE)
	public String createGreeting(@RequestBody @Valid @ModelAttribute Greeting greeting,
			BindingResult errors, Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("greeting", greeting);
			return "greeting/new";
		}

		greetingService.save(greeting);

		return "redirect:/greeting";
	}
}
