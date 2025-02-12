package com.dnd12th_4.pickitalki.controller.login.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String accessToken;
    private String refreshToken;

    private LocalDateTime expiredAccessToken;

    private String userName;

}
