package com.aubay.formations.nr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Error Handler Catch every exceptions and send message error to client
 *
 * @author jbureau@aubay.com
 */
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

	public ResponseEntity<Object> error(final Exception ex, final HttpStatus status, final WebRequest request) {
		LOG.error("Exception occured in application", ex);
		return super.handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), status, request);
	}

	@ExceptionHandler
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		return error(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(final ServletRequestBindingException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleAsyncRequestTimeoutException(final AsyncRequestTimeoutException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest webRequest) {
		return error(ex, status, webRequest);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(final HttpMediaTypeNotAcceptableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(final HttpMessageNotWritableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(final MissingPathVariableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(final ConversionNotSupportedException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status,
			final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,
			final HttpStatus status, final WebRequest request) {
		return error(ex, status, request);
	}
}
