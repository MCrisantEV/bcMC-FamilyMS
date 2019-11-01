package mc.bc.ms.family.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
	
	@Value("${personPort:8004}")
    private String personPort;
	
	@Bean
	public WebClient createWebClient() {
		return WebClient.create("http://localhost:"+personPort+"/persons");
	}
}
