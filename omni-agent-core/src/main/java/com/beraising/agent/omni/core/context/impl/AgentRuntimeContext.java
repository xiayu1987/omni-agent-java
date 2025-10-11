package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.fastjson.annotation.JSONField;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.impl.AgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class AgentRuntimeContext implements IAgentRuntimeContext {

    @JsonDeserialize(contentAs = AgentEvent.class)
    private List<IAgentEvent> agentEvents;
    @JsonIgnore
    private transient IGraphState graphState;
    @JsonIgnore
    private transient IAgent agent;
    @JsonIgnore
    private transient CompiledGraph compiledGraph;
    private String agentName;
    private String agentSessionId;
    private String agentRuntimeContextId;

    @JsonProperty("end")
    private boolean isEnd;

    private int graphRunStatus;

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
    public String getAgentSessionId() {
        return agentSessionId;
    }

    @Override
    public void setAgentSessionId(String agentSessionId) {
        this.agentSessionId = agentSessionId;
    }

    @Override
    public String getAgentRuntimeContextId() {
        return agentRuntimeContextId;
    }

    @Override
    public void setAgentRuntimeContextId(String agentRuntimeContextId) {
        this.agentRuntimeContextId = agentRuntimeContextId;
    }

    @Override
    public int getGraphRunStatus() {
        return graphRunStatus;
    }

    @Override
    public void setGraphRunStatus(int graphRunStatus) {
        this.graphRunStatus = graphRunStatus;
    }

    @Override
    public List<IAgentEvent> getAgentEventsByUserType(EUserType userType) {
        return agentEvents.stream().filter(agentEvent -> agentEvent.getUserType().equals(userType)).toList();
    }

    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    @Override
    public IAgentEvent getCurrentEvent() {
        return ListUtils.lastOf(agentEvents);
    }

}
