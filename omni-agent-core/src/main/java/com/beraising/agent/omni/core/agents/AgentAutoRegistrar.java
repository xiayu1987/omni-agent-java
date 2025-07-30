package com.beraising.agent.omni.core.agents;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class AgentAutoRegistrar implements ApplicationRunner {

    private final List<IAgent> agents;
    private final AgentRegistry registry;

    public AgentAutoRegistrar(List<IAgent> agents, AgentRegistry registry) {
        this.agents = agents;
        this.registry = registry;

        for (IAgent agent : this.agents) {
            this.registry.register(agent);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        // for (IAgent agent : agents) {
        //     registry.register(agent);
        // }
    }
}
