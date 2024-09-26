package com.ezpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main entry point for the EzPayApplication. This class bootstraps the Spring
 * Boot application and initializes all the necessary components.
 */
@SpringBootApplication
public class EzPayApplication {
	/**
	 * Main method that serves as the entry point of the Spring Boot application. It
	 * starts the application by invoking
	 * {@link SpringApplication#run(Class, String[])}.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(EzPayApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**") // Allow all API routes
						.allowedOriginPatterns("*") // Your React app's URL
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowCredentials(true);
			}
		};
	}
}
