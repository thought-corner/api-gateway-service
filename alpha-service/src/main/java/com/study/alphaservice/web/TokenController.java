package com.study.alphaservice.web;

import com.study.alphaservice.jwt.JwtTokenProvider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게이트웨이의 AuthorizationHeader 필터를 확인하기 위한 토큰 발급 엔드포인트.
 * 실제 시스템이라면 인증 전용 서비스가 가져갈 책임이다.
 */
@RestController
@RequestMapping("/alpha-service")
public class TokenController {

	private static final String DEMO_SUBJECT = "test-user";

	private final JwtTokenProvider tokenProvider;

	public TokenController(JwtTokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/token")
	public TokenResponse issue() {
		return TokenResponse.from(tokenProvider.issue(DEMO_SUBJECT));
	}
}
