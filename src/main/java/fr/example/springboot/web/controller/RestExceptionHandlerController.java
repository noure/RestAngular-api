package fr.example.springboot.web.controller;

import fr.example.springboot.ApiErrors;
import fr.example.springboot.exception.InvalidRequestException;
import fr.example.springboot.exception.ResourceNotFoundException;
import fr.example.springboot.exception.UsernameAlreadyUsedException;
import fr.example.springboot.web.dto.ResponseMessage;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Called when an exception occurs during request processing. Transforms
 * exception message into JSON format.
 */
@ControllerAdvice()
public class RestExceptionHandlerController {

	private static final Logger log = LoggerFactory.getLogger(RestExceptionHandlerController.class);

	@Inject
	private MessageSource messageSource;

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	@ResponseBody
	public ResponseEntity<ResponseMessage> handleGenericException(Exception ex, WebRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("handling exception...");
		}
		return new ResponseEntity<>(new ResponseMessage(ResponseMessage.Type.DANGER, ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { ResourceNotFoundException.class })
	@ResponseBody
	public ResponseEntity<ResponseMessage> handleResourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("handling ResourceNotFoundException...");
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { UsernameAlreadyUsedException.class })
	@ResponseBody
	public ResponseEntity<ResponseMessage> handleUsernameExistedException(UsernameAlreadyUsedException ex,
			WebRequest request) {
		if (log.isDebugEnabled()) {
			log.debug("handling UsernameExistedException...");
		}

		ResponseMessage error = ResponseMessage.danger("username existed.");
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { InvalidRequestException.class })
	@ResponseBody
	public ResponseEntity<ResponseMessage> handleInvalidRequestException(InvalidRequestException ex, WebRequest req) {
		if (log.isDebugEnabled()) {
			log.debug("handling InvalidRequestException...");
		}

		ResponseMessage alert = new ResponseMessage(ResponseMessage.Type.DANGER, ApiErrors.INVALID_REQUEST,
				messageSource.getMessage(ApiErrors.INVALID_REQUEST, new String[] {}, null));

		BindingResult result = ex.getErrors();

		List<FieldError> fieldErrors = result.getFieldErrors();

		if (!fieldErrors.isEmpty()) {
			fieldErrors.stream().forEach((e) -> {
				alert.addError(e.getField(), e.getCode(), e.getDefaultMessage());
			});
		}

		return new ResponseEntity<>(alert, HttpStatus.UNPROCESSABLE_ENTITY);
	}

}
