package hello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.dao.GreetingService;
import hello.entities.Greeting;
import hello.exceptions.GreetingNotFoundException;
import jakarta.validation.Valid;

@Controller
@RequestMapping(value = "/greetings", produces = MediaType.TEXT_HTML_VALUE)
public class GreetingController {

	private static final String[] NAMES = { "Rob", "Caroline", "Markel", "Mustafa", "Tom" };

	@Autowired
	private GreetingService greetingService;

	@ExceptionHandler(GreetingNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String greetingNotFoundHandler(GreetingNotFoundException ex, Model model) {

		model.addAttribute("not_found_id", ex.getId());

		return "greetings/not_found";
	}

	@GetMapping
	public String list(Model model) {

		model.addAttribute("greetings", greetingService.findAll());
		model.addAttribute("names", NAMES);

		return "greetings/index";
	}

	@GetMapping("/{id}")
	public String greeting(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {

		Greeting greeting = greetingService.findById(id).orElseThrow(() -> new GreetingNotFoundException(id));

		model.addAttribute("greeting", greeting.getGreeting(name));

		return "greetings/show";
	}

	@GetMapping("/new")
	public String newGreeting(Model model) {
		if (!model.containsAttribute("greeting")) {
			model.addAttribute("greeting", new Greeting());
		}

		return "greetings/new";
	}

	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createGreeting(@RequestBody @Valid @ModelAttribute Greeting greeting, BindingResult errors,
			Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			model.addAttribute("greeting", greeting);
			return "greetings/new";
		}

		greetingService.save(greeting);
		redirectAttrs.addFlashAttribute("ok_message", "New greeting added.");

		return "redirect:/greetings";
	}

	@DeleteMapping("/{id}")
	public String deleteGreeting(@PathVariable("id") long id) {
		greetingService.deleteById(id);

		return "redirect:/greetings";
	}

	@DeleteMapping
	public String deleteAllGreetings() {
		greetingService.deleteAll();

		return "redirect:/greetings";
	}
}
