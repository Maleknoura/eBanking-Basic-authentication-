package org.wora.ebanking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return new ErrorDto("Username already exists", HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleGeneralException(Exception ex) {
        return new ErrorDto("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException ex) {
        ErrorDto errorDto = new ErrorDto("ACCESS_DENIED", HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(errorDto, HttpStatus.FORBIDDEN);
    }
}
