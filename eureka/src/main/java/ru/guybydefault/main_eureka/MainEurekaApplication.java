package ru.guybydefault.main_eureka;

import com.netflix.discovery.shared.transport.EurekaHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.http.RestTemplateEurekaHttpClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MainEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainEurekaApplication.class, args);
	}

}
