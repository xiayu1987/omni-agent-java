package com.beraising.agent.omni.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
		"com.beraising.agent.omni"
})
public class OmniAgentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniAgentServiceApplication.class, args);
	}

}
