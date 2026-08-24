package com.study.apigateway.common;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenValidatorTest {

	private static final String SECRET = "test-secret-key-for-hs256-at-least-32-bytes";
	private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	private final JwtTokenValidator validator = new JwtTokenValidator(new JwtProperties(SECRET));

	@Test
	void 유효한_토큰이면_통과한다() {
		String token = tokenBuilder("test-user").signWith(KEY).compact();

		assertThat(validator.isValid(token)).isTrue();
	}

	@Test
	void 서명_키가_다르면_거부한다() {
		SecretKey otherKey = Keys.hmacShaKeyFor(
			"another-secret-key-that-is-long-enough-32".getBytes(StandardCharsets.UTF_8));
		String token = tokenBuilder("test-user").signWith(otherKey).compact();

		assertThat(validator.isValid(token)).isFalse();
	}

	@Test
	void 만료된_토큰은_거부한다() {
		Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
		String token = Jwts.builder()
			.subject("test-user")
			.issuedAt(Date.from(past.minus(1, ChronoUnit.HOURS)))
			.expiration(Date.from(past))
			.signWith(KEY)
			.compact();

		assertThat(validator.isValid(token)).isFalse();
	}

	@Test
	void subject_가_없으면_거부한다() {
		String token = Jwts.builder()
			.expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
			.signWith(KEY)
			.compact();

		assertThat(validator.isValid(token)).isFalse();
	}

	@Test
	void JWT_형식이_아니면_거부한다() {
		assertThat(validator.isValid("not-a-jwt")).isFalse();
		assertThat(validator.isValid("")).isFalse();
	}

	private static io.jsonwebtoken.JwtBuilder tokenBuilder(String subject) {
		return Jwts.builder()
			.subject(subject)
			.issuedAt(Date.from(Instant.now()))
			.expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));
	}
}
