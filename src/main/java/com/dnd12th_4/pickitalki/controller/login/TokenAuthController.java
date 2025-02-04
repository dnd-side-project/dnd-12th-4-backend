package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.error.TokenErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.mysql.cj.conf.PropertyKey.logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenAuthController {

    private final KaKaoSignUpService kaKaoSignUpService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @Parameter(hidden=true)
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        Member user = kaKaoSignUpService.findUser(refreshToken);

        if (jwtProvider.isTokenExpired(user.getRefreshToken())) {
            executeExpiredCookie(response, user);

             throw new ApiException(TokenErrorCode.EXPIRED_TOKEN,"ResponseEntity refreshAccessToken 에러");
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
