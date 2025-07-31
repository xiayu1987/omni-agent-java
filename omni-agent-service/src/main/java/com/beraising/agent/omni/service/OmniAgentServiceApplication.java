package com.beraising.agent.omni.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import com.beraising.agent.omni.core.agents.OmniAgent;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
    "com.beraising.agent.omni"
})
public class OmniAgentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniAgentServiceApplication.class, args);
	}

	@Bean
	@Order(1)
	public CommandLineRunner start(OmniAgent omniAgent,
			ConfigurableApplicationContext context) {

		return args -> {

			omniAgent.init(null);
			omniAgent.invoke(null);

			context.close();
		};
	}

}
