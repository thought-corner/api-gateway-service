package com.study.alphaservice.jwt;

import com.study.alphaservice.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 토큰 발급 책임만 갖는 컴포넌트. 서명 키는 여기서만 다룬다.
 */
@Component
public class JwtTokenProvider {

	private final SecretKey signingKey;
	private final Duration expiration;

	public JwtTokenProvider(JwtProperties properties) {
		this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
		this.expiration = properties.expiration();
	}

	public IssuedToken issue(String subject) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(expiration);

		String value = Jwts.builder()
			.subject(subject)
			.issuedAt(Date.from(issuedAt))
			.expiration(Date.from(expiresAt))
			.signWith(signingKey)
			.compact();

		return new IssuedToken(value, expiresAt);
	}

	/** 발급된 토큰과 만료 시각 */
	public record IssuedToken(String value, Instant expiresAt) {
	}
}
