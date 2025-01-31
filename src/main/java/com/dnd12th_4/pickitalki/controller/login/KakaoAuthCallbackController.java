package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.controller.login.dto.UserResponse;
import com.dnd12th_4.pickitalki.domain.member.MemberEntity;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import com.dnd12th_4.pickitalki.service.login.KakaoAuthService;
import com.dnd12th_4.pickitalki.service.login.KakaoUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

    private final KakaoUserService kakaoUserService;
    private final KaKaoSignUpService kaKaoSignUpService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @GetMapping("/kakao/callback")
    public ResponseEntity<UserResponse> kakaoCallback(
            @RequestParam("accessToken") String accessToken,
            HttpServletResponse response) {

        KakaoUserDto kakaoUser = kakaoUserService.getUserInfo(accessToken);
        MemberEntity memberEntity = kaKaoSignUpService.registerOrLoginKakaoUser(kakaoUser);

        String refreshToken = memberEntity.getRefreshToken() != null ?
                memberEntity.getRefreshToken() : jwtProvider.createRefreshToken();
        memberEntity.setRefreshToken(refreshToken);
        MemberEntity member = kaKaoSignUpService.saveUserEntity(memberEntity);

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

    private UserResponse toUserResponse(MemberEntity user, String newAccessToken) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .token(newAccessToken)
                .build();
        return userResponse;
    }

}
