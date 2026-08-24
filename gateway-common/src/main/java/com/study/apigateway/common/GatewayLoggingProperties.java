package com.study.apigateway.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * 전역 로깅 필터의 동작 설정.
 *
 * @param baseMessage 로그 앞에 붙일 식별 문구
 * @param preLogger   라우팅 전 로그 출력 여부
 * @param postLogger  라우팅 후 로그 출력 여부
 */
@ConfigurationProperties(prefix = "gateway.logging")
public record GatewayLoggingProperties(
	@DefaultValue("Spring Cloud Gateway Global Filter") String baseMessage,
	@DefaultValue("true") boolean preLogger,
	@DefaultValue("true") boolean postLogger
) {
}
