package com.study.apigateway.webmvc.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * 라우트별로 선택 적용하는 로깅 필터.
 *
 * WebFlux 쪽 RequestLoggingGatewayFilterFactory 와 같은 역할이지만 모델이 다르다.
 * 리액티브 체인이 아니라 요청을 그대로 붙잡고 있는 블로킹 호출이므로
 * next.handle(request) 앞뒤에 그냥 로그를 찍으면 된다.
 */
public final class RequestLoggingFilters {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilters.class);

	private RequestLoggingFilters() {
	}

	public static HandlerFilterFunction<ServerResponse, ServerResponse> requestLogging(String baseMessage) {
		return (request, next) -> {
			log.info("[{}] PRE: {} {}", baseMessage, request.method(), request.path());
			ServerResponse response = next.handle(request);
			log.info("[{}] POST: response code -> {}", baseMessage, response.statusCode());
			return response;
		};
	}
}
