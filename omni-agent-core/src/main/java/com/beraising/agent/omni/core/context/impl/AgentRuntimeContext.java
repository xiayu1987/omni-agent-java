package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AgentRuntimeContext implements IAgentRuntimeContext {

    private List<IAgentEvent> agentEvents;
    @JsonIgnore
    private transient IGraphState graphState;
    @JsonIgnore
    private transient IAgent agent;
    @JsonIgnore
    private transient CompiledGraph compiledGraph;
    private String agentName;
    private String agentSessionID;
    private String agentRuntimeContextID;

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
    public String getAgentName() {
        return agentName;
    }

    @Override
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public String getAgentSessionID() {
        return agentSessionID;
    }

    @Override
    public void setAgentSessionID(String agentSessionID) {
        this.agentSessionID = agentSessionID;
    }

    @Override
    public String getAgentRuntimeContextID() {
        return agentRuntimeContextID;
    }

    @Override
    public void setAgentRuntimeContextID(String agentRuntimeContextID) {
        this.agentRuntimeContextID = agentRuntimeContextID;
    }

}
