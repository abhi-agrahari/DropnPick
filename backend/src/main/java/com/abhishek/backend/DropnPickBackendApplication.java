package com.abhishek.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DropnPickBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropnPickBackendApplication.class, args);
	}

}
