package com.xinliangjishipin.pushwms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PushwmsApplication {
	public static void main(String[] args) {
		SpringApplication.run(PushwmsApplication.class, args);
	}
}
