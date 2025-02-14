package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.token.JwtProvider;
import com.dnd12th_4.pickitalki.controller.login.dto.response.KakaoUserDto;
import com.dnd12th_4.pickitalki.controller.login.dto.response.UserResponse;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.login.KaKaoSignUpService;
import com.dnd12th_4.pickitalki.service.login.KakaoUserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class KakaoAuthExchangeController {

    private final KakaoUserService kakaoUserService;
    private final KaKaoSignUpService kaKaoSignUpService;
    private final JwtProvider jwtProvider;

    @GetMapping("/kakao/exchange")
    public Api<UserResponse> kakaoCallback(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = false) String accessToken) {

        KakaoUserDto kakaoUser = kakaoUserService.getUserInfo(accessToken);

        Member memberEntity = kaKaoSignUpService.registerOrLoginKakaoUser(kakaoUser);

        String refreshToken = memberEntity.getRefreshToken() != null ?
                memberEntity.getRefreshToken() : jwtProvider.createRefreshToken();
        memberEntity.setRefreshToken(refreshToken);
        Member member = kaKaoSignUpService.saveUserEntity(memberEntity);


        String newAccessToken = jwtProvider.createAccessToken(member.getId());
        UserResponse userResponse = toUserResponse( member, newAccessToken,refreshToken,jwtProvider.getTokenExpiration(newAccessToken),member.getNickName());

        return Api.OK(userResponse);
    }

    private UserResponse toUserResponse(Member member, String newAccessToken, String refreshToken, long tokenExpiration, String userName) {
        List<ChannelMember> channelMembers = member.getChannelMembers();
        String channelId = (channelMembers != null && channelMembers.size() == 1)
                ? channelMembers.get(0).getChannel().getUuid().toString()
                : null;

        UserResponse userResponse = UserResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiredAccessToken(tokenExpiration)
                .userName(userName)
                .channelCount(channelMembers.size())
                .channelId(channelId)
                .build();
        return userResponse;
    }

//    private void executeCookie(HttpServletResponse response, String refreshToken) {
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION,"void executeCookie 55번째줄 에러");
//        }
//
//        Cookie cookie = cookieProvider.makeNewCookie("refreshToken", refreshToken);
//        response.addCookie(cookie);
//    }

}
