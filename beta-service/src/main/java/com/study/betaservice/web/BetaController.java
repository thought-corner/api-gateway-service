package com.study.betaservice.web;

import com.study.betaservice.support.ServicePortHolder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/beta-service")
@Slf4j
public class BetaController {

	private final ServicePortHolder portHolder;

	public BetaController(ServicePortHolder portHolder) {
		this.portHolder = portHolder;
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the Beta service.";
	}

	/** 게이트웨이의 AddRequestHeader 가 실제로 붙는지 확인한다 */
	@GetMapping("/message")
	public String message(@RequestHeader("beta-request") String header) {
		log.info("beta-request header={}", header);
		return "Hello World in Beta Service.";
	}

	/** 어느 인스턴스가 처리했는지 확인한다 */
	@GetMapping("/check")
	public String check() {
		return "Hi, there. This is a message from Beta Service on PORT " + portHolder.port();
	}
}
