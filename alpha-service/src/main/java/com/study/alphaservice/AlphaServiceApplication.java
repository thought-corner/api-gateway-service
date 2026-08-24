package com.study.alphaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AlphaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlphaServiceApplication.class, args);
	}

}
