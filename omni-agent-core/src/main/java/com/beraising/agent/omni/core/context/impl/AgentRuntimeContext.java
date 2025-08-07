package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public class AgentRuntimeContext implements IAgentRuntimeContext {

    private IGraphState graphState;
    private List<IAgentEvent> agentEvents;
    private IAgent agent;
    private CompiledGraph compiledGraph;
    private String agentSessionID;

    private boolean isEnd;

    public AgentRuntimeContext() {
        super();
        this.agentEvents = new ArrayList<>();
        this.isEnd = false;
    }

    public IGraphState getGraphState() {
        return graphState;
    }

    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public List<IAgentEvent> getAgentEvents() {
        return agentEvents;
    }

    @Override
    public void setAgentEvents(List<IAgentEvent> agentEvents) {
        this.agentEvents = agentEvents;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public CompiledGraph getCompiledGraph() {
        return compiledGraph;
    }

    @Override
    public void setCompiledGraph(CompiledGraph compiledGraph) {
        this.compiledGraph = compiledGraph;
    }

    @Override
    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    @Override
    public String getAgentSessionID() {
        return agentSessionID;
    }

    @Override
    public void setAgentSessionID(String agentSessionID) {
        this.agentSessionID = agentSessionID;
    }

}
