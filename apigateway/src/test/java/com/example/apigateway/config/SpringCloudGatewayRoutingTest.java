package com.example.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpringCloudGatewayRoutingTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    public void testRoutesConfiguration() {
        Map<String, String> expectedRoutes = Map.of(
                "/accounts/**", "http://localhost:8084",
                "/billings/**", "lb://billingservice",
                "/investments/**", "lb://investmentservice",
                "/transactions/**", "lb://transactionservice"
        );

        // Check if all expected routes are configured
        expectedRoutes.forEach((path, uri) -> {
            Predicate<Route> routePredicate = route ->
                    route.getPredicate().toString().contains(path) &&
                            route.getUri().toString().equals(uri);

            boolean routeExists = Boolean.TRUE.equals(routeLocator.getRoutes()
                    .filter(routePredicate)
                    .hasElements()
                    .block());

            assertThat(routeExists).isTrue();
        });
    }
}
