package com.study.betaservice.support;

import org.springframework.boot.web.server.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 실제로 바인딩된 서버 포트를 보관한다.
 *
 * {@code server.port: 0} 으로 임의 포트를 쓰면 설정값과 실제 포트가 다르므로
 * 웹 서버 기동 이벤트에서 실제 포트를 받아 둔다.
 */
@Component
public class ServicePortHolder implements ApplicationListener<WebServerInitializedEvent> {

	private volatile int port;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		this.port = event.getWebServer().getPort();
	}

	public int port() {
		return port;
	}
}
