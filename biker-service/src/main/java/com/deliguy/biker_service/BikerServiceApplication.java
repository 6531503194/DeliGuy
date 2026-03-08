package com.deliguy.biker_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BikerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BikerServiceApplication.class, args);
	}

}
