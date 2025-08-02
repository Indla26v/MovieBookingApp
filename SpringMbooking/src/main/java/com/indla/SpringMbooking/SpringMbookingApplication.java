package com.indla.SpringMbooking;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMbookingApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringMbookingApplication.class, args);
	}
}