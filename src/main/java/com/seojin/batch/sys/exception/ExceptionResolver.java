package com.seojin.batch.sys.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Description :  Exception Resolver 
 * <p>
 * 
 */
@RestControllerAdvice
public class ExceptionResolver {
	/**
	 * Description : URL Not Found Exception Resolver
	 * <p>
	 * @param e
	 * @param request
	 * @return
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
		Map<String, String> response = new HashMap<>();
		response.put("status", HttpStatus.NOT_FOUND.toString());
		response.put("message", e.getLocalizedMessage());
		return response;
	}

}
