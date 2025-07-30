package com.beraising.agent.omni.core.agents;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public List<IAgent> getAgentsByName(String name) {
        return agentMap.values().stream()
                .filter(agent -> agent.getName().equals(name))
                .collect(Collectors.toList());
    }

    public Map<String, IAgent> getAgentMap() {
        return agentMap;
    }
}