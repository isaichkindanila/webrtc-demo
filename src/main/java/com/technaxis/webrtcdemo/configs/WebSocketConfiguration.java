package com.technaxis.webrtcdemo.configs;

import com.technaxis.webrtcdemo.ws.ICESignaler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final ICESignaler iceSignaler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(iceSignaler, "/iceSignaler").setAllowedOriginPatterns("*");
    }
}
