package com.example.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Route;

import java.util.List;
import java.util.Map;

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

        // Get all routes from the RouteLocator
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        expectedRoutes.forEach((path, uri) -> {
            boolean routeExists = routes.stream()
                    .anyMatch(route -> route.getPredicate().toString().contains(path) &&
                            route.getUri().toString().equals(uri));

            assertThat(routeExists).isTrue();
        });
    }
}
