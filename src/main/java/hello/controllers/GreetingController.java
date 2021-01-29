package hello.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.dao.GreetingService;
import hello.entities.Greeting;

@Controller
@RequestMapping(value = "/greeting", produces = MediaType.TEXT_HTML_VALUE)
public class GreetingController {

	private static final String[] NAMES = { "Rob", "Caroline", "Markel", "Mustafa" };

	@Autowired
	private GreetingService greetingService;

	@GetMapping
	public String list(Model model) {

		model.addAttribute("greetings", greetingService.findAll());
		model.addAttribute("names", NAMES);

		return "greeting/index";
	}

	@GetMapping("/{id}")
	public String greeting(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Greeting greeting = greetingService.findOne(id);
		model.addAttribute("greeting", greeting.getGreeting(name));

		return "greeting/show";
	}

	@GetMapping("/new")
	public String newGreeting(Model model) {
		if (!model.containsAttribute("greeting")) {
			model.addAttribute("greeting", new Greeting());
		}

		return "greeting/new";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createGreeting(@RequestBody @Valid @ModelAttribute Greeting greeting, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("greeting", greeting);
			return "greeting/new";
		}

		greetingService.save(greeting);
		redirectAttrs.addFlashAttribute("ok_message", "New greeting added.");

		return "redirect:/greeting";
	}
}
