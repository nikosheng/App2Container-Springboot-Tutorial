package com.app2container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App2containerApplication {

	public static void main(String[] args) {
		SpringApplication.run(App2containerApplication.class, args);
	}

	@GetMapping("/")
	public String message() {
		return "Hello Spring v1";
	}
}
