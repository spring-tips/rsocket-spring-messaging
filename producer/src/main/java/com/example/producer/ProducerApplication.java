package com.example.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
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


@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingsRequest {
	private String name;
}

@Data
@NoArgsConstructor
class GreetingsResponse {

	private String greeting;

	GreetingsResponse(String name) {
		this.greeting = "Hello " + name + " @ " + Instant.now();
	}
}

@Controller
class GreetingsRSocketController {

	@MessageMapping("greet-over-time")
	Flux<GreetingsResponse> greetOverTime(GreetingsRequest request) {
		return Flux
			.fromStream(Stream.generate(() -> new GreetingsResponse(request.getName())))
			.delayElements(Duration.ofSeconds(1));
	}

	@MessageMapping("greet")
	GreetingsResponse greet(GreetingsRequest request) {
		return new GreetingsResponse(request.getName());
	}

	@MessageMapping("error")
	Publisher<GreetingsResponse> error() {
		return Mono.error(new IllegalArgumentException());
	}

	@MessageExceptionHandler
	Publisher<GreetingsResponse> ohNo(IllegalArgumentException iae) {
		var gr = new GreetingsResponse();
		gr.setGreeting("OH NO!");
		return Mono.just(gr);
	}
}