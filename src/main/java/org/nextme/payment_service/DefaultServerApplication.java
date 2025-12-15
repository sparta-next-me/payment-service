package org.nextme.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackages = "org.nextme")
public class DefaultServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefaultServerApplication.class, args);
	}

}
