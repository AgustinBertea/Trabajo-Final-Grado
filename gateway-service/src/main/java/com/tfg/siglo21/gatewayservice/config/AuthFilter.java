package com.tfg.siglo21.gatewayservice.config;

import com.tfg.siglo21.gatewayservice.dto.TokenDTO;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClient;

    public AuthFilter(WebClient.Builder webClient) {
        super(Config.class);
        this.webClient = webClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, HttpStatus.BAD_REQUEST);
            }
            String tokenHeader = Objects.requireNonNull(exchange
                    .getRequest()
                    .getHeaders()
                    .get(HttpHeaders.AUTHORIZATION))
                    .get(0);
            String[] chunks = tokenHeader.split(" ");
            if(chunks.length !=2 || !chunks[0].equals("Bearer")){
                return onError(exchange, HttpStatus.BAD_REQUEST);
            }
            return webClient.build()
                    .post()
                    .uri("localhost:8001/api/v1/users/auth/validate?token=" + chunks[1])
                    .retrieve()
                    .bodyToMono(TokenDTO.class)
                    .map(t -> {
                        t.getToken();
                        return exchange;
                    }).flatMap(chain::filter);
        }));
    }

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {}
}