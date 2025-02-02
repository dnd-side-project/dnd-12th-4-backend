package com.dnd12th_4.pickitalki.presentation.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode implements ErrorCodeIfs {

    INVALID_TOKEN(401,2000,"유효하지 않은 토큰"),
    EXPIRED_TOKEN(401,2001,"만료된 토큰"),

    TOKEN_EXCEPTION(400,2002,"알수 없는 토큰 에러"),
    AUTHORIZATION_TOKEN_NOT_FOUND(400,2003,"인증 헤더 코드 없음"),
    ;


    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String description;
}
