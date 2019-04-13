package com.example.consumer;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;

@SpringBootApplication
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Bean
	RSocket rSocket() {
		return RSocketFactory
			.connect()
			.dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
			.frameDecoder(PayloadDecoder.ZERO_COPY)
			.transport(TcpClientTransport.create(7000))
			.start()
			.block();
	}

	@Bean
	RSocketRequester requester(RSocketStrategies rSocketStrategies) {
		return RSocketRequester.create(this.rSocket(),
			MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
	}
}


@RestController
class GreetingsRestController {

	private final RSocketRequester requester;

	GreetingsRestController(RSocketRequester requester) {
		this.requester = requester;
	}

	@GetMapping("/error")
	Publisher<GreetingsResponse> error() {
		return this.requester
			.route("error")
			.data(Mono.empty())
			.retrieveFlux(GreetingsResponse.class);
	}

	@GetMapping(
		produces = MediaType.TEXT_EVENT_STREAM_VALUE,
		value = "/greet/sse/{name}")
	Publisher<GreetingsResponse> greetStream(@PathVariable String name) {
		return this.requester
			.route("greet-stream")
			.data(new GreetingsRequest(name))
			.retrieveFlux(GreetingsResponse.class);
	}

	@GetMapping("/greet/{name}")
	Publisher<GreetingsResponse> greet(@PathVariable String name) {
		return this.requester
			.route("greet")
			.data(new GreetingsRequest(name))
			.retrieveMono(GreetingsResponse.class);
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
