package com.dnd12th_4.pickitalki.controller.login.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    private String refreshToken;
    private Long expiredAccessToken;

}
