package com.smartcanteen;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class SmartCanteenBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCanteenBackendApplication.class, args);
	}

	@PostConstruct
	public void setSecurityContextHolderStrategy() {
		// This ensures the SecurityContext is managed per thread, which is crucial for web requests.
		// It's the default, but explicitly setting it can resolve subtle timing/propagation issues.
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
		// If you later have async operations that need security context, you might switch to MODE_INHERITABLE_THREAD_LOCAL
	}


}
