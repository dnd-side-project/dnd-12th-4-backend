package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.service.login.KakaoAuthService;
import com.dnd12th_4.pickitalki.service.login.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class KakaoAuthCallbackController {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoUserService kakaoUserService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoUserDto> kakaoCallback(@RequestParam("code") String code) {
        String accessToken = kakaoAuthService.getAccessToken(code);
        KakaoUserDto kakaoUser = kakaoUserService.getUserInfo(accessToken);

        return ResponseEntity.ok(kakaoUser);
    }
}
