package com.example.rsocketmessaging.client;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

@RestController
class ConsumerController {

	private final RSocketRequester requester;

	ConsumerController(RSocketRequester requester) {
		this.requester = requester;
	}

	@GetMapping("/greet/timed/{name}")
	Publisher<String> greetOverTime(@PathVariable String name) {
		return requester
			.route("greet-over-time")
			.data(name)
			.retrieveFlux(String.class);
	}

	@GetMapping("/greet/{name}")
	Publisher<String> greet(@PathVariable String name) {
		return requester.route("greet").data(name).retrieveFlux(String.class);
	}

	@GetMapping("/greet/faf/{name}")
	Publisher<Void> sendGreetings(@PathVariable String name) {
		return requester.route("send-greetings").data(name).send();
	}

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE,
		value = "/greet/infinite/{name}")
	Publisher<String> infiniteGreetings(@PathVariable String name) {

		var out = Flux
			.fromStream(Stream.generate(() -> "Yo @ " + Instant.now()))
			.delayElements(Duration.ofSeconds(1));

		return requester.route("infinite-greetings")
			.data(out, String.class)
			.retrieveFlux(String.class);

	}
}
