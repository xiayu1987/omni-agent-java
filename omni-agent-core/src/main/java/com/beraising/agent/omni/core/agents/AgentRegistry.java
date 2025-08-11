package com.beraising.agent.omni.core.agents;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class AgentRegistry {
    private final Map<String, IAgent> agentMap = new ConcurrentHashMap<>();

    public void register(IAgent agent) {
        agentMap.put(agent.getName(), agent);
    }

    public IAgent getAgent(String name) {
        return agentMap.get(name);
    }

    public IAgent getAgentByName(String name) {
        return agentMap.values().stream()
                .filter(agent -> agent.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public IRouterAgent getRouterAgent() {
        return (IRouterAgent) agentMap.values().stream()
                .filter(agent -> agent instanceof IRouterAgent)
                .findFirst()
                .orElse(null);
    }

    public Map<String, IAgent> getAgentMap() {
        return agentMap;
    }
}