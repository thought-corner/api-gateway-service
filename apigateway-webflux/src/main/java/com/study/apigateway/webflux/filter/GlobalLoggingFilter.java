package com.study.apigateway.webflux.filter;

import com.study.apigateway.common.GatewayLoggingProperties;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 모든 라우트에 적용되는 로깅 필터.
 *
 * 전역 적용이 목적이므로 GatewayFilterFactory 를 만들어 default-filters 에 등록하는 대신
 * Spring Cloud Gateway 가 제공하는 {@link GlobalFilter} 를 구현한다.
 * 빈으로 등록되기만 하면 자동으로 전 라우트에 적용되므로 yml 설정이 필요 없다.
 */
@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

	private final GatewayLoggingProperties properties;

	public GlobalLoggingFilter(GatewayLoggingProperties properties) {
		this.properties = properties;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		if (properties.preLogger()) {
			log.info("[{}] 요청 시작: {} {} from {}", properties.baseMessage(), request.getMethod(),
				request.getURI().getPath(), request.getRemoteAddress());
		}

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			if (properties.postLogger()) {
				log.info("[{}] 요청 종료: {} {} -> {}", properties.baseMessage(), request.getMethod(),
					request.getURI().getPath(), response.getStatusCode());
			}
		}));
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
