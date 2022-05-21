package com.db.awmd.challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.db.awmd.challenge.domain.ErrorMessage;

@ControllerAdvice
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{

	/**
	 * @param exception
	 * @param request
	 * @return ErrorMessage containing HttpStatus and message
	 */
	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<ErrorMessage> insufficientBalanceException(InsufficientFundsException exception, WebRequest request){
		ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST,
				exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
	}
	
	/**
	 * @param exception
	 * @param request
	 * @return ErrorMessage containing HttpStatus and message
	 */
	@ExceptionHandler(AccountInfoNotFoundException.class)
	public ResponseEntity<ErrorMessage> accountInfoNotFoundException(AccountInfoNotFoundException exception, WebRequest request){
		ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND,
				exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	}
	
}
