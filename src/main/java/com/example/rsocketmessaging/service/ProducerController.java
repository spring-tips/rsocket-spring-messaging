package com.example.rsocketmessaging.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@Log4j2
@Controller
class ProducerController {

	@MessageMapping("greet")
	String greet(String name) {
		return doGreet(name);
	}

	@MessageMapping("greet-over-time")
	Flux<String> greetOverTime(String name) {
		return Flux
			.fromStream(Stream.generate(() -> doGreet(name)))
			.delayElements(Duration.ofSeconds(1));
	}

	@MessageMapping("send-greetings")
	Mono<Void> sendGreetings(String name) {
		log.info("new greetings has arrived: " + name);
		return Mono.empty();
	}

	@MessageMapping("infinite-greetings")
	Flux<String> infiniteGreetings(Flux<String> incoming) {
		return incoming.map(str -> "Echo:  " + str);
	}

	private static String doGreet(String name) {
		return "hello " + name + " @  " + Instant.now();
	}
}
