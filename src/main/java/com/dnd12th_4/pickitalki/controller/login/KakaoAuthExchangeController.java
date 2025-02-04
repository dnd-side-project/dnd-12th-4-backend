package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.cookie.CookieProvider;
import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.controller.login.dto.UserResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.presentation.error.TokenErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import com.dnd12th_4.pickitalki.service.login.KakaoUserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    public Api<UserResponse> kakaoCallback(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            HttpServletResponse response) {

        KakaoUserDto kakaoUser = kakaoUserService.getUserInfo(accessToken);

        boolean isNewMember= false;

        Member memberEntity = kaKaoSignUpService.registerOrLoginKakaoUser(kakaoUser, isNewMember);

        String refreshToken = memberEntity.getRefreshToken() != null ?
                memberEntity.getRefreshToken() : jwtProvider.createRefreshToken();
        memberEntity.setRefreshToken(refreshToken);
        Member member = kaKaoSignUpService.saveUserEntity(memberEntity);

        executeCookie(response, refreshToken);

        String newAccessToken = jwtProvider.createAccessToken(member.getId());
        UserResponse userResponse = toUserResponse( newAccessToken,isNewMember);

        return Api.OK(userResponse);
    }

    private void executeCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION,"void executeCookie 55번째줄 에러");
        }

        Cookie cookie = cookieProvider.makeNewCookie("refreshToken", refreshToken);
        response.addCookie(cookie);
    }

    private UserResponse toUserResponse(String newAccessToken,boolean isNewMember) {
        UserResponse userResponse = UserResponse.builder()
                .isNewMember(isNewMember)
                .token(newAccessToken)
                .build();
        return userResponse;
    }

}
