package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.controller.login.dto.UserResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import com.dnd12th_4.pickitalki.service.login.KakaoUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class KakaoAuthExchangeController {

    private final KakaoUserService kakaoUserService;
    private final KaKaoSignUpService kaKaoSignUpService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @GetMapping("/kakao/exchange")
    public ResponseEntity<UserResponse> kakaoCallback(
            @RequestHeader("Authorization") String accessToken,
            HttpServletResponse response) {

        KakaoUserDto kakaoUser = kakaoUserService.getUserInfo(accessToken);
        Member memberEntity = kaKaoSignUpService.registerOrLoginKakaoUser(kakaoUser);

        String refreshToken = memberEntity.getRefreshToken() != null ?
                memberEntity.getRefreshToken() : jwtProvider.createRefreshToken();
        memberEntity.setRefreshToken(refreshToken);
        Member member = kaKaoSignUpService.saveUserEntity(memberEntity);

        executeCookie(response, refreshToken);

        String newAccessToken = jwtProvider.createAccessToken(memberEntity.getId());
        UserResponse userResponse = toUserResponse(member, newAccessToken);

        return ResponseEntity.ok(userResponse);
    }

    private void executeCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh Token cannot be null or empty");
        }

        Cookie cookie = cookieProvider.makeNewCookie("refreshToken", refreshToken);
        response.addCookie(cookie);
    }

    private UserResponse toUserResponse(Member user, String newAccessToken) {
        UserResponse userResponse = UserResponse.builder()
                .token(newAccessToken)
                .build();
        return userResponse;
    }

}
