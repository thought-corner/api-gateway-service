package com.study.apigateway.webmvc.filter;

import com.study.apigateway.common.GatewayLoggingProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 요청에 적용되는 로깅 필터.
 *
 * WebFlux 게이트웨이는 GlobalFilter 인터페이스를 구현하면 전 라우트에 자동 적용되지만,
 * Gateway MVC 에는 그에 해당하는 확장점도 default-filters 설정도 없다.
 * 서블릿 스택이므로 표준 서블릿 필터가 그 자리를 대신한다.
 *
 * 대신 적용 범위가 다르다. GlobalFilter 는 게이트웨이 라우트에만 걸리지만
 * 서블릿 필터는 actuator 를 포함한 모든 요청을 지나간다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalLoggingFilter extends OncePerRequestFilter {

	private final GatewayLoggingProperties properties;

	public GlobalLoggingFilter(GatewayLoggingProperties properties) {
		this.properties = properties;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (properties.preLogger()) {
			log.info("[{}] 요청 시작: {} {} from {}", properties.baseMessage(),
					request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
		}
		try {
			chain.doFilter(request, response);
		} finally {
			if (properties.postLogger()) {
				log.info("[{}] 요청 종료: {} {} -> {}", properties.baseMessage(),
						request.getMethod(), request.getRequestURI(), response.getStatus());
			}
		}
	}
}
