package com.daimlertrucksasia.it.dsc.localization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the PIGEON localization microservice.
 * <p>
 * This service is part of the registry ecosystem and is responsible for providing
 * localized message resolution and storage. It registers itself with a Eureka
 * service registry for service discovery.
 * </p>
 *
 * <p>The {@code @EnableDiscoveryClient} annotation enables the microservice
 * to register with a discovery server such as Eureka, allowing it to be located
 * by other services (e.g., via Spring Cloud Gateway).</p>
 *
 * <p>The {@code @SpringBootApplication} annotation enables auto-configuration,
 * component scanning, and Spring Boot's configuration model.</p>
 *
 * <p><b>Usage:</b> Run this class as a Java application to start the service.</p>
 *
 *
 * @since 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
public class DSCLocalizationApplication {

	/**
	 * The main method that boots the Spring Boot application.
	 *
	 * @param args command-line arguments (none required).
	 */
	public static void main(String[] args) {
		SpringApplication.run(DSCLocalizationApplication.class, args);
	}
}
