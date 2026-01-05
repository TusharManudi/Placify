package com.app.placify.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),e.getMessage() , HttpStatus.NOT_FOUND.value() );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentials e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),e.getMessage() , HttpStatus.BAD_REQUEST.value() );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(ExternalServiceException e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),e.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR.value() );
        return new ResponseEntity<>(response ,  HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),e.getMessage() , HttpStatus.BAD_REQUEST.value() );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(),"Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value() );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
