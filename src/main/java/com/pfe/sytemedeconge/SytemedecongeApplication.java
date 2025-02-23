package com.pfe.sytemedeconge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "Model") // Spécifier où Hibernate doit chercher les entités
@EnableJpaRepositories(basePackages = "Repository")

public class SytemedecongeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SytemedecongeApplication.class, args);
	}

}
