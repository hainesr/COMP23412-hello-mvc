package hello.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GreetingNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(GreetingNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String greetingNotFoundHandler(GreetingNotFoundException ex) {
		return ex.getMessage();
	}
}
