package com.dnd12th_4.pickitalki.controller.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class KakaoUserDto {

    private String id;
    private String email;
    private String nickname;
    private String profileImageUrl;

    private String token;
}
