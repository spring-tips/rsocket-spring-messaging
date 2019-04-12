package com.example.rsocketmessaging.client;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

@Configuration
class ConsumerConfiguration {

	@Bean
	RSocket rSocket() {
		return RSocketFactory
			.connect()
			.dataMimeType(MimeTypeUtils.TEXT_PLAIN_VALUE)
			.frameDecoder(PayloadDecoder.ZERO_COPY)
			.transport(TcpClientTransport.create(7000))
			.start()
			.block();
	}

	@Bean
	RSocketRequester requester(RSocketStrategies strategies, RSocket rSocket) {
		return RSocketRequester.create(rSocket, MimeTypeUtils.TEXT_PLAIN, strategies);
	}
}
