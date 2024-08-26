package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudGatewayRouting {

    @Bean
    public RouteLocator configureRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("accountservice", r -> r.path("/accounts/**")
                        .uri("http://localhost:8084"))
                .route("billingservice", r -> r.path("/billings/**")
                        .uri("lb://billingservice"))
                .route("investmentservice", r -> r.path("/investments/**")
                        .uri("lb://investmentservice"))
                .route("transactionservice", r -> r.path("/transactions/**")
                        .uri("lb://transactionservice"))
                .build();
    }

}
