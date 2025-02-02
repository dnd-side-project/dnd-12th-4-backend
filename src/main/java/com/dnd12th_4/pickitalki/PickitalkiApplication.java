package com.dnd12th_4.pickitalki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PickitalkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickitalkiApplication.class, args);
	}

}
