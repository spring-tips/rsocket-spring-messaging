package com.example.rsocketmessaging.service;

import io.netty.buffer.PooledByteBufAllocator;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.messaging.rsocket.MessageHandlerAcceptor;
import org.springframework.messaging.rsocket.RSocketStrategies;

import javax.annotation.PostConstruct;

@Log4j2
@Configuration
class ProducerConfiguration {

	@Bean
	MessageHandlerAcceptor messageHandlerAcceptor() {
		var mha = new MessageHandlerAcceptor();
		mha.setRSocketStrategies(rSocketStrategies());
		return mha;
	}

	@Bean
	RSocketStrategies rSocketStrategies() {
		return RSocketStrategies
			.builder()
			.decoder(StringDecoder.allMimeTypes())
			.encoder(CharSequenceEncoder.allMimeTypes())
			.dataBufferFactory(new NettyDataBufferFactory(PooledByteBufAllocator.DEFAULT))
			.build();
	}

	@PostConstruct
	public void serve() {
		RSocketFactory
			.receive()
			.frameDecoder(PayloadDecoder.ZERO_COPY)
			.acceptor(messageHandlerAcceptor())
			.transport(TcpServerTransport.create(7000))
			.start()
			.block();
	}

}
