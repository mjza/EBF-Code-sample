package com.mjzsoft.odata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.mjzsoft.odata.entities.Employee;
import com.mjzsoft.odata.repository.EmployeeRepository;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner demo(final EmployeeRepository repository) {
	    return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				if (repository.count() == 0) {
					log.info("Database is still empty. Adding some sample records");
					repository.save(new Employee(1, "Jack", "Bauer"));
					repository.save(new Employee(2, "Chloe", "O'Brian"));
					repository.save(new Employee(3, "Kim", "Bauer"));
					repository.save(new Employee(4, "David", "Palmer"));
					repository.save(new Employee(5, "Michelle", "Dessler"));
				}
	        }
	    };
	}
	
	

}