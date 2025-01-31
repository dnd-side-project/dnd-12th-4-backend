package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoConfig kakaoConfig;

    //클라이언트와의 회의를 통해 해당 클래스 사용 안합니다.
    @GetMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin() {

        String KAKAO_AUTH_URL = kakaoConfig.getKakaoAuthUrl();
        String CLIENT_ID = kakaoConfig.getClientId();
        String REDIRECT_URI = kakaoConfig.getRedirectUri();


        String url = KAKAO_AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "response_type=code";
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", url).build();
    }
}
