package com.daimlertrucksasia.it.dsc.healthmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DSCHealthMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DSCHealthMonitorApplication.class, args);
	}

}
