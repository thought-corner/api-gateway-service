package com.study.alphaservice.web;

import com.study.alphaservice.jwt.JwtTokenProvider.IssuedToken;

import java.time.Instant;

public record TokenResponse(String accessToken, String tokenType, Instant expiresAt) {

	public static TokenResponse from(IssuedToken token) {
		return new TokenResponse(token.value(), "Bearer", token.expiresAt());
	}
}
