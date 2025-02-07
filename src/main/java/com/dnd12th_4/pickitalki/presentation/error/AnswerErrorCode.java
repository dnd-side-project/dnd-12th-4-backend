package com.dnd12th_4.pickitalki.presentation.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnswerErrorCode implements ErrorCodeIfs {

    INVALID_ARGUMENT(400,400,"잘못된 요청")
   ;


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;


}
