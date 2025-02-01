package com.dnd12th_4.pickitalki.controller.login.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoConfig {
    @Value("${kakao.auth-url}")
    private String kakaoAuthUrl;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    @Value("${kakao.client-secret}")
    private String clientSecret;
}
