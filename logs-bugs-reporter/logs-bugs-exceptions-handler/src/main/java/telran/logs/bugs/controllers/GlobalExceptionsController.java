package telran.logs.bugs.controllers;

import javax.validation.ConstraintViolationException;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.logs.bugs.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionsController {

	static Logger LOG = LoggerFactory.getLogger(GlobalExceptionsController.class);
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String constraintViolationHandler(ConstraintViolationException e) {
		return processingExceptions(e);
		
	}
	
	@ExceptionHandler(DuplicatedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String duplicateKeyHandler(DuplicatedException e) {
		return processingExceptions(e);
		
	}
	
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFounHandler(NotFoundException e) {
		return processingExceptions(e);
	}
	
	@ExceptionHandler(ServerException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	String serverExceptionHandler(ServerException e) {
		return processingExceptions(e);
	}

	private String processingExceptions(Exception e) {
		LOG.error("exception class: {}, massage: {}", e.getClass().getSimpleName(), e.getMessage());
		return e.getMessage();
	}
}
