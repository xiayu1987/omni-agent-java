package com.beraising.agent.omni.core.agents.introduce.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.introduce.IIntroduceAgent;
import com.beraising.agent.omni.core.agents.introduce.graph.IIntroduceGraph;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;

@Component
public class IntroduceAgent extends AgentBase implements IIntroduceAgent {

    private final IIntroduceGraph introduceGraph;
    private final IAgentStaticContext agentStaticContext;

    public IntroduceAgent(IIntroduceGraph introduceGraph, IAgentStaticContext agentStaticContext) {
        this.introduceGraph = introduceGraph;
        this.agentStaticContext = agentStaticContext;
    }

    @Override
    public String getName() {
        return "Introduce Agent";
    }

    @Override
    public String getDescription() {
        return "功能介绍agent,主要介绍当前可用Agents所具备的所有功能";
    }

    @Override
    public IAgentStaticContext getAgentStaticContext() {
        return agentStaticContext;
    }

    @Override
    public IAgentGraph getAgentGraph() {
        return introduceGraph;
    }

}
