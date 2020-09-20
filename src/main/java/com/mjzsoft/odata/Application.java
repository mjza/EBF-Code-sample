package com.mjzsoft.odata;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {
		logger.info("Application has been started.");		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		ConfigurableEnvironment environment = new StandardEnvironment();
		environment.setActiveProfiles("dev");
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.setEnvironment(environment);
		springApplication.run(args);
		
	}

}
