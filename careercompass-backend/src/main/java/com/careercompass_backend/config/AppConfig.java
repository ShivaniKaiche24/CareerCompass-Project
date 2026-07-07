package com.careercompass_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
	
	/*
	 * RestTemplate is Spring's HTTP client.
	 * We use it to call the Gemini API from our service layer.
	 * 
	 * Why declare it as a @Bean instead of using new RestTemplate()?
     * Making it a bean lets Spring manage it — it can be injected anywhere,
     * and in tests we can swap it with a mock. If we used new RestTemplate()
     * inside GeminiService, the class would be impossible to unit test.
     *
     * Note: for new projects Spring recommends WebClient (reactive),
     * but RestTemplate is simpler and perfectly fine for synchronous calls.
   
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
