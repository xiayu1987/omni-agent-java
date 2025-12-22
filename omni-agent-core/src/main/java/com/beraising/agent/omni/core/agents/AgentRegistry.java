package com.beraising.agent.omni.core.agents;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.intent.IIntentAgent;
/**
 * Agent注册中心，用于管理和注册各种类型的Agent实例
 */
@Component
public class AgentRegistry {
    private final Map<String, IAgent> agentMap = new ConcurrentHashMap<>();

    /**
     * 注册一个Agent实例
     * @param agent 要注册的Agent实例，不能为空
     */
    public void register(IAgent agent) {
        agentMap.put(agent.getName(), agent);
    }

    /**
     * 根据名称获取Agent实例
     * @param name Agent名称
     * @return 对应的Agent实例，如果不存在则返回null
     */
    public IAgent getAgent(String name) {
        return agentMap.get(name);
    }

    /**
     * 通过遍历方式根据名称查找Agent实例
     * @param name 要查找的Agent名称
     * @return 匹配名称的Agent实例，如果不存在则返回null
     */
    public IAgent getAgentByName(String name) {
        return agentMap.values().stream()
                .filter(agent -> agent.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取RouterAgent实例
     * @return RouterAgent实例，如果不存在则返回null
     */
    public IRouterAgent getRouterAgent() {
        return (IRouterAgent) agentMap.values().stream()
                .filter(agent -> agent instanceof IRouterAgent)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取IntentAgent实例
     * @return IntentAgent实例，如果不存在则返回null
     */
    public IIntentAgent getIntentAgent() {
        return (IIntentAgent) agentMap.values().stream()
                .filter(agent -> agent instanceof IIntentAgent)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有CustomAgent实例列表
     * @return CustomAgent实例列表
     */
    public List<ICustomAgent> getAllCustomAgents() {
        return agentMap.values().stream()
                .filter(agent -> agent instanceof ICustomAgent)
                .map(agent -> (ICustomAgent) agent)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有已注册的Agent映射表
     * @return 包含所有Agent的Map集合
     */
    public Map<String, IAgent> getAgentMap() {
        return agentMap;
    }
}
