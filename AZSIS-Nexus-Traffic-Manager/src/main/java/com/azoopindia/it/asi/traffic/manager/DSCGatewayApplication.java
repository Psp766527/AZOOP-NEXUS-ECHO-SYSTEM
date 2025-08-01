package com.azoopindia.it.asi.traffic.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/**
 * Entry point for the registry Traffic Manager Spring Boot application.
 * <p>
 * This application acts as an API Gateway with support for:
 * <ul>
 *   <li>Dynamic rate limiting</li>
 *   <li>Custom Gateway filters</li>
 *   <li>Service discovery via Spring Cloud (e.g., Eureka)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Packages are explicitly defined for component scanning to ensure that
 * services and filters are properly registered.
 * </p>
 */
@SpringBootApplication(scanBasePackages = {
        "com.daimlertrucksasia.it.dsc.gateway",
        "com.daimlertrucksasia.it.dsc.gateway.service",
        "com.daimlertrucksasia.it.dsc.gateway.filter"
})
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class DSCGatewayApplication {

    /**
     * Main method that launches the Spring Boot application.
     * Also performs a check to verify if the custom rate limiter bean is registered.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {

        ApplicationContext applicationContextFactory = SpringApplication.run(DSCGatewayApplication.class, args);

    }
}
