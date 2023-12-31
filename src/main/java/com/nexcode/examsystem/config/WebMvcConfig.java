package com.nexcode.examsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	private final long MAX_AGE_SECS = 3600;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
	    .allowedOrigins("http://localhost:5173","http://localhost:5174","http://localhost:5175","http://localhost:5176") 
	    .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
	    .maxAge(MAX_AGE_SECS)
	    .allowedHeaders("*")
	    .allowCredentials(true);		
	}
}
