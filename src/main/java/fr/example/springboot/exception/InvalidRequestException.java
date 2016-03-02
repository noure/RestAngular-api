
package fr.example.springboot.exception;

import org.springframework.validation.BindingResult;

/**
 *
 * @author Nour LABIHI
 */
public class InvalidRequestException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BindingResult errors;

    public InvalidRequestException(BindingResult errors) {
        this.errors = errors;
    }

    public BindingResult getErrors() {
        return errors;
    }

}
