package com.dnd12th_4.pickitalki.presentation.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeIfs{

    OK(200,200,"성공"),
        BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), 400,"잘못된 요청"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),500,"서버 에러"),
    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(),512,"Null Pointer" ),
    DUPLICATED_MEMBER(HttpStatus.BAD_REQUEST.value(),499,"중복된 멤버")
    ;


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;

}
