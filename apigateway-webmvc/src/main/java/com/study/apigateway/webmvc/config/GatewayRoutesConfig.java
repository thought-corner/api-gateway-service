package com.study.apigateway.webmvc.config;

import com.study.apigateway.common.JwtTokenValidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static com.study.apigateway.webmvc.filter.AuthorizationHeaderFilters.authorizationHeader;
import static com.study.apigateway.webmvc.filter.RequestLoggingFilters.requestLogging;
import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.addResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.addRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.removeRequestHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

/**
 * 라우트 정의.
 *
 * WebFlux 게이트웨이는 같은 내용을 application.yml 에 쓰지만, Gateway MVC 는 Java DSL 이 기본이다.
 * yml 로도 쓸 수 있으나 커스텀 필터를 이름으로 참조하려면 FilterSupplier 빈을 따로 등록해야 해서
 * 여기서는 DSL 을 쓴다. 필터를 그냥 빈으로 주입받을 수 있는 것도 DSL 쪽 장점이다.
 *
 * RouterFunction 은 등록된 순서대로 평가되므로 더 구체적인 경로를 먼저 선언한다.
 */
@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouterFunction<ServerResponse> alphaServiceSecureRoute(JwtTokenValidator validator) {
		return route("alpha-service-secure")
			.route(path("/alpha-service/secure/**"), http())
			.before(removeRequestHeader("Cookie"))
			.filter(authorizationHeader(validator))
			.filter(lb("ALPHA-SERVICE"))
			.build();
	}

	@Bean
	public RouterFunction<ServerResponse> alphaServiceRoute() {
		return route("alpha-service")
			.route(path("/alpha-service/**"), http())
			.before(addRequestHeader("alpha-request", "alpha-request-header"))
			.after(addResponseHeader("alpha-response", "alpha-response-header"))
			.filter(requestLogging("Alpha route"))
			.filter(lb("ALPHA-SERVICE"))
			.build();
	}

	@Bean
	public RouterFunction<ServerResponse> betaServiceRoute() {
		return route("beta-service")
			.route(path("/beta-service/**"), http())
			.before(addRequestHeader("beta-request", "beta-request-header"))
			.after(addResponseHeader("beta-response", "beta-response-header"))
			.filter(requestLogging("Beta route"))
			.filter(lb("BETA-SERVICE"))
			.build();
	}
}
