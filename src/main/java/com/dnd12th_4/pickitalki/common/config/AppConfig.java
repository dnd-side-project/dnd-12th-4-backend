package com.dnd12th_4.pickitalki.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${app.base-url}")
    private static String baseUrl;

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
