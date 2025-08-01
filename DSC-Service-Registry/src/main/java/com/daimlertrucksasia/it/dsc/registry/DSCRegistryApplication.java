package com.daimlertrucksasia.it.dsc.registry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main class for starting the registry Eureka Server application.
 * <p>
 * This application serves as a Eureka Service Registry, enabling service discovery
 * for microservices in a Spring Cloud ecosystem.
 * </p>
 *
 * <p>
 * Annotations used:
 * <ul>
 *   <li>{@link SpringBootApplication} - Marks this class as a Spring Boot application.</li>
 *   <li>{@link EnableEurekaServer} - Enables this application to act as a Eureka Server.</li>
 * </ul>
 * </p>
 *
 * <p>
 * After the application starts, it will be accessible (by default) at:
 * {@code http://localhost:8761/} to view registered services.
 * </p>
 *
 * @since 1.0
 */
@SpringBootApplication
@EnableEurekaServer
public class DSCRegistryApplication implements CommandLineRunner {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Entry point of the Eureka Server application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SpringApplication.run(DSCRegistryApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(">>> ACTIVE PROFILE: " + activeProfile);
    }
}
