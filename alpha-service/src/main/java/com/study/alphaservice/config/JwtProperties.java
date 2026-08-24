package com.study.alphaservice.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	@NotBlank
	@Size(min = 32, message = "HS256 서명에는 최소 32바이트(256bit) 키가 필요합니다")
	String secret,

	@NotNull
	Duration expiration
) {
}
