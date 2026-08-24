package com.study.apigateway.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * WebFlux(Netty) 기반 API 게이트웨이.
 *
 * gateway-common 의 클래스도 함께 스캔해야 하므로 스캔 기준 패키지를 상위로 올린다.
 */
@SpringBootApplication(scanBasePackages = "com.study.apigateway")
@ConfigurationPropertiesScan("com.study.apigateway")
public class WebfluxGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxGatewayApplication.class, args);
	}

}
