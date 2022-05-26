package com.github.piotrostrow.chess.security;

import com.github.piotrostrow.chess.security.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketAuthConfig.class);

	private final JwtUtil jwtUtil;

	public WebSocketAuthConfig(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
					Optional.ofNullable(accessor.getNativeHeader("auth"))
							.flatMap(e -> e.stream().findFirst())
							.flatMap(jwtUtil::getAuthentication)
							.ifPresentOrElse(accessor::setUser, () -> LOGGER.error("Auth failed for websocket session {}", accessor.getSessionId()));
				}
				return message;
			}
		});
	}
}