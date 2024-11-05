package com.reliaquest.api.exception;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import lombok.extern.slf4j.Slf4j;
import com.reliaquest.api.model.ErrorResponse;

/**
 * Error Response builder to handle 4XX and 5XX error codes
 */
@Slf4j
@Component
public class ErrorResponseBuilder {
	
	private ErrorResponseBuilder( ) {
		//private constructor
	}
	
    public static RestClientException exceptionByStatus(ResponseEntity<?> response) {
        byte[] bodyBytes = (response.hasBody()) ? response.getBody().toString().getBytes() : null;
        log.info("Response error from Server: {}", response.getBody());
        if (response.getStatusCode().is4xxClientError()) {
            return HttpClientErrorException.create(
                    response.getStatusCode(), "Client Error", response.getHeaders(), bodyBytes, StandardCharsets.UTF_8);
        }
        if (response.getStatusCode().is5xxServerError()) {
        	return HttpServerErrorException.create(
        			response.getStatusCode(),
        			"Server Side Error",
        			response.getHeaders(),
        			bodyBytes,
        			StandardCharsets.UTF_8);
        }
        return new RestClientException("");
    }
    
    public ResponseEntity<ErrorResponse> createErrorResponse(HttpStatusCodeException exception, String data) {
    	ErrorResponse errorResponse = new ErrorResponse();
    	errorResponse.setMessage(data+ " not present");
    	errorResponse.setStatus(exception.getStatusText());
    	return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
