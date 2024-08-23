package com.example.investmentservice.config;

import com.example.investmentservice.exception.CustomFeignErrorDecoder;
import com.example.investmentservice.exception.GlobalExceptionHandler;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
