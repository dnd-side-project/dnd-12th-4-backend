package com.dnd12th_4.pickitalki.service.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoConfig;
import com.dnd12th_4.pickitalki.service.login.tool.HttpCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoConfig kakaoConfig;
    private final HttpCreator httpCreator;

    public String getAccessToken(String code) {

        ResponseEntity<Map> response = httpCreator.getResponseAccessToken(code);

        return response.getBody().get("access_token").toString();
    }

}
