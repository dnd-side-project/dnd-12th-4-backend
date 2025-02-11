package com.dnd12th_4.pickitalki.presentation.exceptionhandler;

import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCodeIfs;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import org.springframework.core.annotation.Order;

import org.springframework.dao.DataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(value = Integer.MIN_VALUE) // 예외 우선순위가 가장 높다
public class ApiExceptionHandler {

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> apiException(
            ApiException apiException
    ) {
        ErrorCodeIfs errorCode = apiException.getErrorCodeIfs();

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(
                  Api.ERROR(errorCode,apiException.getErrorDescription())
                );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> apiException(
            IllegalArgumentException exception
    ) {;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        exception.getMessage()
                );
    }

    @ExceptionHandler(value = DataAccessException.class)
    public ResponseEntity<Object> databaseException(
            DataAccessException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        exception.getMessage()
                );

    }
}
