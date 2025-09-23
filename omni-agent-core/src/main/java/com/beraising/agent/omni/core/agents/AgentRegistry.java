package com.beraising.agent.omni.core.agents;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.intent.IIntentAgent;

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

    public IIntentAgent getIntentAgent() {
        return (IIntentAgent) agentMap.values().stream()
                .filter(agent -> agent instanceof IIntentAgent)
                .findFirst()
                .orElse(null);
    }

    public List<ICustomAgent> getAllCustomAgents() {
        return agentMap.values().stream()
                .filter(agent -> agent instanceof ICustomAgent)
                .map(agent -> (ICustomAgent) agent)
                .collect(Collectors.toList());
    }

    public Map<String, IAgent> getAgentMap() {
        return agentMap;
    }
}