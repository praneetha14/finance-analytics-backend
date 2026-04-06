package com.finance.analytics.rest.v1;

import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.InvalidInputException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.exception.UserUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import com.finance.analytics.model.vo.FailureResponseVO;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<FailureResponseVO> handleInvalidInputException(InvalidInputException ex){
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<FailureResponseVO> handleUnauthorizedException(UserUnauthorizedException ex) {
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<FailureResponseVO> handleNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<FailureResponseVO> handleDuplicateException(DuplicateResourceException ex){
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.CONFLICT.value(), ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailureResponseVO> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.FORBIDDEN.value(), "Access Denied: You do not have the required permissions to perform this action."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailureResponseVO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<FailureResponseVO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Invalid input format. Please check your data types and enum values.";
        if (ex.getMessage() != null && ex.getMessage().contains("RecordTypeEnum")) {
            message = "Invalid recordType. Please use 'INCOME' or 'EXPENSE' (case-sensitive).";
        }
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.BAD_REQUEST.value(), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<FailureResponseVO> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password. Please check your credentials."), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FailureResponseVO> handleIllegalArgumentException(IllegalArgumentException ex){
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FailureResponseVO> handleRuntimeException(RuntimeException ex){
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailureResponseVO> handleGlobalException(Exception ex) {
        return new ResponseEntity<>(FailureResponseVO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}