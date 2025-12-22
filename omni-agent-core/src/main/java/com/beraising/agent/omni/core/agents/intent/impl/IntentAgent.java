package com.beraising.agent.omni.core.agents.intent.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.intent.IIntentAgent;
import com.beraising.agent.omni.core.agents.intent.graph.IIntentGraph;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;
/**
 * 意图代理类，负责识别用户意图并返回相应的意图对应的代理
 * 实现了IIntentAgent接口，继承自AgentBase基类
 */
@Component
public class IntentAgent extends AgentBase implements IIntentAgent {

    private final IIntentGraph intentGraph;
    private final IAgentStaticContext agentStaticContext;

    /**
     * 构造函数
     * @param intentGraph 意图图谱接口实例，用于处理意图相关的图谱操作
     * @param agentStaticContext 代理静态上下文接口实例，提供代理的静态上下文信息
     */
    public IntentAgent(IIntentGraph intentGraph, IAgentStaticContext agentStaticContext) {
        this.intentGraph = intentGraph;
        this.agentStaticContext = agentStaticContext;
    }

    /**
     * 获取代理名称
     * @return 返回代理的名称字符串"Intent Agent"
     */
    @Override
    public String getName() {
        return "Intent Agent";
    }

    /**
     * 获取代理描述信息
     * @return 返回代理的功能描述字符串，说明该代理用于意图识别并返回相应意图对应的代理
     */
    @Override
    public String getDescription() {
        return "意图识别agent,主要识别用户的意图并返回相应意图对应agent";
    }

    /**
     * 获取代理静态上下文
     * @return 返回代理静态上下文接口实例
     */
    @Override
    public IAgentStaticContext getAgentStaticContext() {
        return agentStaticContext;
    }

    /**
     * 获取代理图谱
     * @return 返回意图图谱接口实例
     */
    @Override
    public IAgentGraph getAgentGraph() {
        return intentGraph;
    }

}

