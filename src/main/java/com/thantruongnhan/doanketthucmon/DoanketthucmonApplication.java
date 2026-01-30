package com.thantruongnhan.doanketthucmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DoanketthucmonApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoanketthucmonApplication.class, args);
	}

}
