package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenAuthController {

    private final KaKaoSignUpService kaKaoSignUpService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {

        Member user = kaKaoSignUpService.findUser(refreshToken);

        if (jwtProvider.isTokenExpired(user.getRefreshToken())) {
            executeExpiredCookie(response, user);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Refresh token expired. Please log in again."));
        }

        String newAccessToken = jwtProvider.createAccessToken(user.getId());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    private void executeExpiredCookie(HttpServletResponse response, Member user) {
        Cookie cookie = cookieProvider.makeExpiredCookie("refreshToken", null);
        response.addCookie(cookie);

        user.setRefreshToken(null);
        kaKaoSignUpService.saveUserEntity(user);
    }
}
