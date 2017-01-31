package hello.helpers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ErrorHelpers {
	public static Map<String, String> formErrorHelper(BindingResult errors) {
		Map<String, String> fieldErrors = new HashMap<String, String>();

		for (FieldError f : errors.getFieldErrors()) {
			fieldErrors.put(f.getField(), f.getDefaultMessage());
		}

		return fieldErrors;
	}
}
