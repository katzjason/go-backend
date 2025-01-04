package com.example.go_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoBackendApplication.class, args);
	}

	public int Add(int a, int b){
		return a + b;
	}

}
