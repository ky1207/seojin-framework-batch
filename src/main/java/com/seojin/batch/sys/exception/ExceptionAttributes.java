package com.seojin.batch.sys.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;

/**
 * Description : Exception Attributes interface 구성
 * <p>
 *
 */
public interface ExceptionAttributes {

	/**
	 * Description : A Map of exception attributes.
	 * <p>
	 * @param exception
	 * @param httpRequest
	 * @param httpStatus
	 * @return
	 */
	Map<String, Object> getExceptionAttributes(Exception exception, HttpServletRequest httpRequest, HttpStatus httpStatus);

	/**
	 * Description : A Map of exception attributes.
	 * <p>
	 * @param exception
	 * @param httpStatus
	 * @return
	 */
	Map<String, Object> getExceptionAttributes(String exception, HttpStatus httpStatus);

}