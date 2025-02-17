package com.dnd12th_4.pickitalki.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Getter
public class AppConfig {

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
