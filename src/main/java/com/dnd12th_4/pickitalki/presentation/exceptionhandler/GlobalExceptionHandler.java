package com.dnd12th_4.pickitalki.presentation.exceptionhandler;

import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(value = Integer.MAX_VALUE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Api<Object>> exception(
            Exception exception
    ){
        log.error("❌ [HTTP 500] Internal Server Error: ", exception);

        return ResponseEntity.status(500)
                .body(
                        Api.ERROR(ErrorCode.SERVER_ERROR,exception.getMessage())
                );
    }
}
