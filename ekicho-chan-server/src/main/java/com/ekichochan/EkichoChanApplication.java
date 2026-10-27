package com.ekichochan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.ekichochan.repositories") 
public class EkichoChanApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkichoChanApplication.class, args);
		System.out.println("Spring Boot Start!");
	}

}
