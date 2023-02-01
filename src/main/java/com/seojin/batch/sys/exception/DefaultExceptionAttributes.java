package com.seojin.batch.sys.exception;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

/**
 * Description : Exception Attributes interface 구현
 * <p>
 *
 */
public class DefaultExceptionAttributes implements ExceptionAttributes {

	/**
	 * The timestamp attribute key.
	 */
	public static final String TIMESTAMP = "timestamp";
	/**
	 * The status attribute key.
	 */
	public static final String STATUS = "status";
	/**
	 * The error attribute key.
	 */
	public static final String ERROR = "error";
	/**
	 * The exception attribute key.
	 */
	public static final String EXCEPTION = "exception";
	/**
	 * The message attribute key.
	 */
	public static final String MESSAGE = "message";
	/**
	 * The path attribute key.
	 */
	public static final String PATH = "path";

	@Override
	public Map<String, Object> getExceptionAttributes(Exception exception, HttpServletRequest httpRequest, HttpStatus httpStatus) {
		Map<String, Object> exceptionAttributes = new LinkedHashMap<String, Object>();

		exceptionAttributes.put(TIMESTAMP, new Date());
		addHttpStatus(exceptionAttributes, httpStatus);
		addExceptionDetail(exceptionAttributes, exception);
		addPath(exceptionAttributes, httpRequest);

		return exceptionAttributes;
	}

	@Override
	public Map<String, Object> getExceptionAttributes(String exception, HttpStatus httpStatus) {
		Map<String, Object> exceptionAttributes = new LinkedHashMap<String, Object>();

		exceptionAttributes.put(TIMESTAMP, new Date());
		addHttpStatus(exceptionAttributes, httpStatus);
		exceptionAttributes.put(MESSAGE, exception);

		return exceptionAttributes;
	}

	/**
	 * @param exceptionAttributes
	 * @param httpStatus
	 */
	private void addHttpStatus(Map<String, Object> exceptionAttributes, HttpStatus httpStatus) {
		exceptionAttributes.put(STATUS, httpStatus.value());
		if(!httpStatus.equals(HttpStatus.OK))
			exceptionAttributes.put(ERROR, httpStatus.getReasonPhrase());
	}

	/**
	 * @param exceptionAttributes
	 * @param exception
	 */
	private void addExceptionDetail(Map<String, Object> exceptionAttributes, Exception exception) {
		exceptionAttributes.put(EXCEPTION, exception.getClass().getName());
		exceptionAttributes.put(MESSAGE, exception.getMessage());
	}

	/**
	  * @param exceptionAttributes
	  * @param httpRequest
	  */
	private void addPath(Map<String, Object> exceptionAttributes, HttpServletRequest httpRequest) {
		exceptionAttributes.put(PATH, httpRequest.getServletPath());
	}
}
