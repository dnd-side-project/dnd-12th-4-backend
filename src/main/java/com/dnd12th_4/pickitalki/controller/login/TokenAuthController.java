package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.common.token.SHA256Util;
import com.dnd12th_4.pickitalki.controller.login.dto.response.RefreshTokenResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.api.Api;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Api<RefreshTokenResponse> refreshAccessToken(
            @Parameter(hidden=true)
            @RequestHeader(value = "Authorization", required = false) String refreshToken,
            HttpServletResponse response) {

        refreshToken = subBerar(refreshToken);

        Member user = kaKaoSignUpService.findUser(refreshToken);

        if (jwtProvider.isTokenExpired(refreshToken)) {
            executeExpiredCookie(response, user);

             throw new ApiException(TokenErrorCode.EXPIRED_TOKEN,"ResponseEntity refreshAccessToken 에러");
        }
        String newRefreshToken = makeNewRefreshToken(user);

        RefreshTokenResponse refreshTokenResponse = getRefreshTokenResponse(user, newRefreshToken);

        return Api.OK(refreshTokenResponse);
    }

    private String makeNewRefreshToken(Member user) {
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        String hashedRefreshToken = SHA256Util.hash(newRefreshToken);
        user.setRefreshToken(hashedRefreshToken);
        kaKaoSignUpService.saveUserEntity(user);
        return newRefreshToken;
    }

    private  String subBerar(String refreshToken) {
        if(refreshToken.startsWith("Bearer ")){
            refreshToken = refreshToken.substring(7);
        }
        return refreshToken;
    }

    private RefreshTokenResponse getRefreshTokenResponse(Member user, String newRefreshToken) {

        String newAccessToken = jwtProvider.createAccessToken(user.getId());

        RefreshTokenResponse refreshTokenResponse = RefreshTokenResponse.builder()
                .expiredAccessToken(jwtProvider.getTokenExpiration(newAccessToken))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return refreshTokenResponse;
    }

    private void executeExpiredCookie(HttpServletResponse response, Member user) {

        user.setRefreshToken(null);
        kaKaoSignUpService.saveUserEntity(user);
    }
}
