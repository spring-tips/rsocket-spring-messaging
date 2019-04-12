package com.example.rsocketmessaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RsocketMessagingApplication {

	public static void main(String[] args) {
		var prop = SpringApplication.run(RsocketMessagingApplication.class, args)
			.getEnvironment()
			.getProperty("spring.main.lazy-initialization");
		System.out.println( "true? " + prop);
	}
}
