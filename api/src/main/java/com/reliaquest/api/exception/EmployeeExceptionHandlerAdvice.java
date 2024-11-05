package com.reliaquest.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.ErrorResponse;

import lombok.AllArgsConstructor;

/**
 * Exception Handler Advice to handle all exceptions thrown during Server API interaction
 */
@ControllerAdvice
@AllArgsConstructor
public class EmployeeExceptionHandlerAdvice {

    private final ObjectMapper mapper = new ObjectMapper();

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException.BadRequest ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString(), "Bad Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(HttpClientErrorException.NotFound ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString(), "Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString(), ex.getStatusText());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(
            HttpServerErrorException.InternalServerError ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString(), "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getResponseBodyAsString(), ex.getStatusText());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = parseErrorResponse(ex.getLocalizedMessage().toString(), ex.getCause().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Converting exceptions in a uniform ErrorResponse object
     */
    private ErrorResponse parseErrorResponse(String responseBodyAsString, String status) {
        ErrorResponse errorResponse;
        try {
            errorResponse = mapper.readValue(responseBodyAsString, ErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (errorResponse.getMessage() == null) {
            errorResponse.setMessage(responseBodyAsString);
        }
        if (errorResponse.getStatus() == null) {
            errorResponse.setStatus(status);
        }
        return errorResponse;
    }
}
