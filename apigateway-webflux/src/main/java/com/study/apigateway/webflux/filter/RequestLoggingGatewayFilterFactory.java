package com.study.apigateway.webflux.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 라우트별로 선택 적용하는 로깅 필터.
 *
 * 클래스명이 {@code ...GatewayFilterFactory} 로 끝나면 Spring Cloud Gateway 가 접미사를 떼고
 * 이름을 인식하므로 yml 에서는 {@code RequestLogging} 으로 참조한다.
 *
 * <pre>
 * filters:
 *   - RequestLogging                       # 기본값 사용
 *   - RequestLogging=Beta route            # shortcut 표기 (baseMessage 만 지정)
 *   - name: RequestLogging                 # 전체 표기
 *     args:
 *       base-message: Beta route
 *       pre-logger: true
 *       post-logger: false
 * </pre>
 */
@Slf4j
@Component
public class RequestLoggingGatewayFilterFactory
	extends AbstractGatewayFilterFactory<RequestLoggingGatewayFilterFactory.Config> {

	public RequestLoggingGatewayFilterFactory() {
		super(Config.class);
	}

	/** {@code - RequestLogging=<baseMessage>} 형태의 축약 표기를 허용한다 */
	@Override
	public List<String> shortcutFieldOrder() {
		return List.of("baseMessage");
	}

	@Override
	@NullMarked
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			if (config.isPreLogger()) {
				log.info("[{}] PRE: request id -> {}", config.getBaseMessage(), request.getId());
			}

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				if (config.isPostLogger()) {
					log.info("[{}] POST: response code -> {}", config.getBaseMessage(), response.getStatusCode());
				}
			}));
		}, Ordered.HIGHEST_PRECEDENCE);
	}

	@Getter
	@Setter
	public static class Config {
		private String baseMessage = "Request Logging Filter";
		private boolean preLogger = true;
		private boolean postLogger = true;
	}
}
