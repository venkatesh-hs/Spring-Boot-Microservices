package com.photoapp.api.gateway;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	@Autowired
	Environment env;
	
	public AuthorizationHeaderFilter() {
		super(Config.class);
	}

	public static class Config {
		// put configuration properties here
	}

	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");
			if (!isJwtValid(jwt)) {
				return onError(exchange, "Jwt token invalid", HttpStatus.UNAUTHORIZED);
			}
			return chain.filter(exchange);
		};
	}

	private boolean isJwtValid(String jwt) {
		boolean valid = true;
		String subject = null;

		try {
			subject = Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(jwt).getBody()
					.getSubject();
		} catch (Exception e) {
			valid = false;
			e.printStackTrace();
		}

		if (subject == null || subject.isEmpty()) {
			valid = false;
		}
		return valid;
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus unauthorized) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(unauthorized);
		return response.setComplete();
	}

}
