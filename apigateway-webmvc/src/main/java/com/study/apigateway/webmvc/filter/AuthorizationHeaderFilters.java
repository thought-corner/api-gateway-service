package com.study.apigateway.webmvc.filter;

import com.study.apigateway.common.JwtTokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;

/**
 * {@code Authorization: Bearer <token>} 헤더를 검증하는 필터.
 *
 * 검증 로직은 gateway-common 의 {@link JwtTokenValidator} 를 그대로 쓴다.
 * 웹 스택이 바뀌어도 재사용되는 부분이다.
 */
public final class AuthorizationHeaderFilters {

	private static final Logger log = LoggerFactory.getLogger(AuthorizationHeaderFilters.class);
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String UNAUTHORIZED_BODY =
			"{\"error\":\"unauthorized\",\"message\":\"유효한 토큰이 필요합니다\"}";

	private AuthorizationHeaderFilters() {
	}

	public static HandlerFilterFunction<ServerResponse, ServerResponse> authorizationHeader(JwtTokenValidator validator) {
		return (request, next) -> {
			Optional<String> token = resolveBearerToken(request);
			if (token.filter(validator::isValid).isEmpty()) {
				log.warn("인증 실패: {} {}", request.method(), request.path());
				return ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.contentType(MediaType.APPLICATION_JSON)
						.body(UNAUTHORIZED_BODY);
			}
			return next.handle(request);
		};
	}

	private static Optional<String> resolveBearerToken(ServerRequest request) {
		return request.headers().firstHeader(HttpHeaders.AUTHORIZATION) == null
				? Optional.empty()
				: Optional.of(request.headers().firstHeader(HttpHeaders.AUTHORIZATION))
						.filter(h -> h.startsWith(BEARER_PREFIX))
						.map(h -> h.substring(BEARER_PREFIX.length()).trim())
						.filter(t -> !t.isEmpty());
	}
}
