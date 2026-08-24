package com.study.apigateway.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 검증에 필요한 설정. 잘못된 값은 애플리케이션 기동 시점에 걸러진다.
 *
 * @param secret HMAC 서명 키. HS256 은 256bit 이상을 요구하므로 32바이트 이상이어야 한다.
 */
@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	@NotBlank
	@Size(min = 32, message = "HS256 서명에는 최소 32바이트(256bit) 키가 필요합니다")
	String secret
) {
}
