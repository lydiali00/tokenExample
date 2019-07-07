package com.example.tokenDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:config/spring-redis.xml")
public class TokenDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokenDemoApplication.class, args);
	}

}
