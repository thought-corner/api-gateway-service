package com.study.apigateway.common;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

/**
 * JWT 검증 책임만 갖는 컴포넌트.
 * 서명 키 생성과 파싱을 여기로 모아 두어 필터는 HTTP 관심사만 다루게 한다.
 */
@Slf4j
@Component
public class JwtTokenValidator {

	private final JwtParser parser;

	public JwtTokenValidator(JwtProperties properties) {
		SecretKey signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
		this.parser = Jwts.parser().verifyWith(signingKey).build();
	}

	/** 서명이 유효하고 subject 가 있으면 true */
	public boolean isValid(String token) {
		try {
			String subject = parser.parseSignedClaims(token).getPayload().getSubject();
			return StringUtils.hasText(subject);
		} catch (JwtException | IllegalArgumentException ex) {
			log.debug("JWT 검증 실패: {}", ex.getMessage());
			return false;
		}
	}
}
