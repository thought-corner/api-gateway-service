package com.study.alphaservice.web;

import com.study.alphaservice.support.ServicePortHolder;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alpha-service")
@Slf4j
public class AlphaController {

	private final ServicePortHolder portHolder;

	public AlphaController(ServicePortHolder portHolder) {
		this.portHolder = portHolder;
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the Alpha service.";
	}

	/** 게이트웨이의 AddRequestHeader 가 실제로 붙는지 확인한다 */
	@GetMapping("/message")
	public String message(@RequestHeader("alpha-request") String header) {
		log.info("alpha-request header={}", header);
		return "Hello World in Alpha Service.";
	}

	/** 어느 인스턴스가 처리했는지 확인한다 */
	@GetMapping("/check")
	public String check() {
		return "Hi, there. This is a message from Alpha Service on PORT " + portHolder.port();
	}

	/** 게이트웨이에서 AuthorizationHeader 필터가 걸려 있는 경로 */
	@GetMapping("/secure/hello")
	public String secured() {
		return "You passed the gateway JWT filter.";
	}
}
