package com.beraising.agent.omni.service;

import java.util.HashMap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import com.beraising.agent.omni.core.agents.EAgentRequestType;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentRequest;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
		"com.beraising.agent.omni"
})
public class OmniAgentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniAgentServiceApplication.class, args);
	}

	@Bean
	@Order(1)
	public CommandLineRunner start(IAgentEngine omniAgentEngine,
			ConfigurableApplicationContext context) {

		return args -> {

			omniAgentEngine.invoke(AgentEvent.builder()
					.agentRequest(
							AgentRequest.builder().requestType(EAgentRequestType.TEXT).text("我想明天请假")
									.data(new HashMap<>()).build())
					.agentResponse(null)
					.agentSessionID(null).build());

			context.close();
		};
	}

}
