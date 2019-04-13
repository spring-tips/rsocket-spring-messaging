package com.example.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@SpringBootApplication
public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}
}


@Controller
class GreetingsRSocketController {

	@MessageMapping("error")
	Flux<GreetingsResponse> error() {
		return Flux.error(new IllegalArgumentException());
	}

	@MessageExceptionHandler
	Flux<GreetingsResponse> errorHandler(IllegalArgumentException iae) {
		return Flux.just(new GreetingsResponse()
			.withGreeting("OH NO!"));
	}

	@MessageMapping("greet-stream")
	Flux<GreetingsResponse> greetStream(GreetingsRequest request) {
		return Flux
			.fromStream(Stream.generate(() -> new GreetingsResponse(request.getName())))
			.delayElements(Duration.ofSeconds(1));
	}

	@MessageMapping("greet")
	GreetingsResponse greet(GreetingsRequest request) {
		return new GreetingsResponse(request.getName());
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingsRequest {
	private String name;
}

@Data
class GreetingsResponse {

	private String greeting;

	GreetingsResponse() {
	}

	GreetingsResponse(String name) {
		this.withGreeting("Hello " + name + " @ " + Instant.now());
	}

	GreetingsResponse withGreeting(String msg) {
		this.greeting = msg;
		return this;
	}
}