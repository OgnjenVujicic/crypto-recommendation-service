package com.example.cryptorecommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CryptoRecommendationApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoRecommendationApplication.class, args);
	}

}
