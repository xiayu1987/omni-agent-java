package com.beraising.agent.omni.core.agents.intent.impl;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentBase;
import com.beraising.agent.omni.core.agents.intent.IIntentAgent;
import com.beraising.agent.omni.core.agents.intent.graph.IIntentGraph;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;

@Component
public class IntentAgent extends AgentBase implements IIntentAgent {

    private final IIntentGraph intentGraph;
    private final IAgentStaticContext agentStaticContext;

    public IntentAgent(IIntentGraph intentGraph, IAgentStaticContext agentStaticContext) {
        this.intentGraph = intentGraph;
        this.agentStaticContext = agentStaticContext;
    }

    @Override
    public String getName() {
        return "Intent Agent";
    }

    @Override
    public String getDescription() {
        return "意图识别agent,主要识别用户的意图并返回相应意图对应agent";
    }

    @Override
    public IAgentStaticContext getAgentStaticContext() {
        return agentStaticContext;
    }

    @Override
    public IAgentGraph getAgentGraph() {
        return intentGraph;
    }

}
