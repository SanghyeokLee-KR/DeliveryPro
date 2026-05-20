package com.icia.delivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // "/topic" 경로의 메시지 브로커를 활성화합니다.
        config.setApplicationDestinationPrefixes("/app"); // 애플리케이션 메시지 경로의 접두사를 설정합니다.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notifications") // 엔드포인트를 "/ws-notifications"로 변경
                .setAllowedOriginPatterns("*") // 실제 도메인으로 제한하는 것이 보안상 좋습니다.
                .withSockJS();
    }
}
