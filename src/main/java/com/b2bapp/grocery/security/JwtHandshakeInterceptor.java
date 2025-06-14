package com.b2bapp.grocery.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServletServerHttpRequest;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//
//        if (request instanceof ServletServerHttpRequest servletRequest) {
//            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
//            String authHeader = httpServletRequest.getHeader("Authorization");
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                String email = jwtService.extractUsername(token);
//
//                if (email != null) {
//                    var userDetails = userDetailsService.loadUserByUsername(email);
//                    if (jwtService.isTokenValid(token, userDetails)) {
//                        attributes.put("userEmail", email); // save for controller access
//                        return true;
//                    }
//                }
//            }
//        }
//
//        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception exception) {
//        // nothing
//    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // Do nothing here; we’ll handle it in the ChannelInterceptor
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {}
}
