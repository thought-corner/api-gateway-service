package com.study.apigateway.webflux.filter;

import com.study.apigateway.common.JwtTokenValidator;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * {@code Authorization: Bearer <token>} 헤더를 검증하는 라우트 필터.
 * 토큰 해석은 {@link JwtTokenValidator} 에 위임하고 여기서는 HTTP 관심사만 다룬다.
 * yml 에서는 {@code AuthorizationHeader} 로 참조한다.
 */
@Slf4j
@Component
public class AuthorizationHeaderGatewayFilterFactory
	extends AbstractGatewayFilterFactory<AuthorizationHeaderGatewayFilterFactory.Config> {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final byte[] UNAUTHORIZED_BODY = "{\"error\":\"unauthorized\",\"message\":\"유효한 토큰이 필요합니다\"}".getBytes(
		StandardCharsets.UTF_8);

	private final JwtTokenValidator tokenValidator;

	public AuthorizationHeaderGatewayFilterFactory(JwtTokenValidator tokenValidator) {
		super(Config.class);
		this.tokenValidator = tokenValidator;
	}

	@Override
	@NullMarked
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> resolveBearerToken(exchange.getRequest())
			.filter(tokenValidator::isValid)
			.map(token -> chain.filter(exchange))
			.orElseGet(() -> unauthorized(exchange));
	}

	private Optional<String> resolveBearerToken(ServerHttpRequest request) {
		String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(BEARER_PREFIX)) {
			return Optional.empty();
		}
		String token = header.substring(BEARER_PREFIX.length()).trim();
		return token.isEmpty() ? Optional.empty() : Optional.of(token);
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		log.warn("인증 실패: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI().getPath());

		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBuffer buffer = response.bufferFactory().wrap(UNAUTHORIZED_BODY);
		return response.writeWith(Mono.just(buffer));
	}

	public static class Config {
	}
}
