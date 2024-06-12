package com.example.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.apigateway.util.JwtUtil;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

        @Autowired
    private RestTemplate template;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(ObjectMapper objectMapper) {
        super(Config.class);
        this.objectMapper = objectMapper;
    }

//    public AuthenticationFilter() {
//        super(Config.class);
//    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
        	ServerHttpRequest request= null;
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                System.out.println(authHeader);
                try {
//                    //REST call to AUTH service
//                    template.getForObject("http://user-service:8100/api/user/validate?token=" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);
//                    request = exchange.getRequest()
//                    		.mutate()
//                    		.header("authentication", authHeader)
//                    		.build();

                } catch (Exception e) {
//                    throw new RuntimeException("un authorized access to application");
                	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    ErrorResponse errorResponse = new ErrorResponse("Unauthorized Access");
                    try {
                        String errorMessage = objectMapper.writeValueAsString(errorResponse);
                        return exchange.getResponse().writeWith(
                                Mono.just(exchange.getResponse().bufferFactory().wrap(errorMessage.getBytes())));
                    } catch (JsonProcessingException ex) {
                        return Mono.error(ex);
                    }
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}