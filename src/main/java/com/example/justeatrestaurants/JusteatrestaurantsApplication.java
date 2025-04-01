package com.example.justeatrestaurants;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JusteatrestaurantsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(JusteatrestaurantsApplication.class, args);
	}


	@Override
	public void run(String... args) {
		System.out.println("Welcome to the Just Eat Restaurant Viewer");
	}
}
