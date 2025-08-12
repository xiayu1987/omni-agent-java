package com.beraising.agent.omni.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.beraising.agent.omni"
})
@SpringBootApplication
public class OmniAgentMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmniAgentMcpApplication.class, args);
    }

}
