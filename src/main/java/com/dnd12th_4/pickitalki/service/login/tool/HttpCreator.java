package com.dnd12th_4.pickitalki.service.login.tool;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpCreator {

    private final RestTemplate restTemplate;
    private final KakaoConfig kakaoConfig;

    public ResponseEntity<Map> getResponseAccessToken(String code){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","authorization_code");
        body.add("client_id",kakaoConfig.getClientId());
        body.add("redirect_uri",kakaoConfig.getRedirectUri());
        body.add("code",code);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(body,headers);
        return restTemplate.exchange(kakaoConfig.getTokenUrl(), HttpMethod.POST,request,Map.class);
    }


    public ResponseEntity<Map> getResponseUserInfo(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(kakaoConfig.getUserInfoUrl(), HttpMethod.GET, request, Map.class);
    }
}
