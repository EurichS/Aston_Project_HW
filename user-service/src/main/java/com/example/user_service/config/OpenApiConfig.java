package com.example.user_service.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/users/**")
                .build();
    }
}
