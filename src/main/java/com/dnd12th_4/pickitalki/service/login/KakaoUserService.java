package com.dnd12th_4.pickitalki.service.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoConfig;
import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.controller.login.dto.response.NewMemberStatus;
import com.dnd12th_4.pickitalki.presentation.error.TokenErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.dnd12th_4.pickitalki.service.login.tool.HttpCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final HttpCreator httpCreator;

    public KakaoUserDto getUserInfo(String accessToken) {

        ResponseEntity<Map> response = Optional.ofNullable(accessToken)
                .map(httpCreator::getResponseUserInfo)
                .orElseThrow(() -> new ApiException(TokenErrorCode.INVALID_TOKEN, "accessToken이 없습니다. 등록해주세요"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        Map<String,Object> profile = (Map<String,Object>)kakaoAccount.get("profile");

        String email = kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString()
                : "no-email-" + response.getBody().get("id").toString() + "@kakao.com";

        return KakaoUserDto.builder()
                .id(response.getBody().get("id").toString())
                .email(email)
                .nickname("닉네임 설정필요")
                .profileImageUrl(profile.get("profile_image_url").toString())
                .build();

    }
}
