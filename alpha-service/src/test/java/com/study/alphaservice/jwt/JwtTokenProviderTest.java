package com.study.alphaservice.jwt;

import com.study.alphaservice.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

	private static final String SECRET = "test-secret-key-for-hs256-at-least-32-bytes";
	private static final Duration EXPIRATION = Duration.ofHours(24);

	private final JwtTokenProvider provider =
		new JwtTokenProvider(new JwtProperties(SECRET, EXPIRATION));

	@Test
	void 발급한_토큰은_같은_키로_검증되고_subject_를_담는다() {
		JwtTokenProvider.IssuedToken issued = provider.issue("test-user");

		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
		String subject = Jwts.parser().verifyWith(key).build()
			.parseSignedClaims(issued.value())
			.getPayload()
			.getSubject();

		assertThat(subject).isEqualTo("test-user");
	}

	@Test
	void 만료_시각은_설정한_유효기간을_따른다() {
		Instant before = Instant.now();

		JwtTokenProvider.IssuedToken issued = provider.issue("test-user");

		assertThat(issued.expiresAt())
			.isBetween(before.plus(EXPIRATION).minusSeconds(5), Instant.now().plus(EXPIRATION));
	}
}
