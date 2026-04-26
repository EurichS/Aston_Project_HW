package com.example.user_service.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    /**
     * @ Method Name: modelMapper
     * @ Description: Creates and configures a ModelMapper-bean
     * @ param      : []
     * @ return     : org.modelmapper.ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
