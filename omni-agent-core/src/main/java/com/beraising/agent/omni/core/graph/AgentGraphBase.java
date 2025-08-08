package com.beraising.agent.omni.core.graph;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.state.StateSnapshot;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public abstract class AgentGraphBase<T extends IGraphState> implements IAgentGraph {

    private IAgent agent;
    private IAgentGraphListener agentGraphListener;
    private List<IGraphNode> graphNodes;
    private IEventListener eventListener;

    public AgentGraphBase() {
        super();
        graphNodes = new ArrayList<>();
    }

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    @Override
    public void setAgentGraphListener(IAgentGraphListener agentGraphListener) {
        this.agentGraphListener = agentGraphListener;
    }

    @Override
    public IAgentGraphListener getAgentGraphListener() {
        return agentGraphListener;
    }

    @Override
    public void setEventListener(IEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public IEventListener getEventListener() {
        return eventListener;
    }

    @Override
    public void setGraphNodes(List<IGraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    @Override
    public List<IGraphNode> getGraphNodes() {
        return graphNodes;
    }

    @Override
    public void init(IAgent agent, IEventListener eventListener, IAgentGraphListener agentGraphListener)
            throws Exception {
        this.agent = agent;
        this.agentGraphListener = agentGraphListener;
        this.eventListener = eventListener;

    }

    @Override
    public IAgentEvent invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        if (agentRuntimeContext.getAgentEvents().size() == 0) {
            throw new Exception("AgentRuntimeContext has no AgentEvent");
        }

        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(agentRuntimeContext.getAgentSessionID())
                .build();

        IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());

        if (agentRuntimeContext.getAgentEvents().size() == 1) {

            eventListener.beforeGraphInvoke(agent, agentEvent, agentRuntimeContext);

            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createInput(agentRuntimeContext), runnableConfig);

        }

        if (agentRuntimeContext.getAgentEvents().size() > 1) {

            eventListener.beforeGraphInvoke(agent, agentEvent, agentRuntimeContext);

            StateSnapshot stateSnapshot = agentRuntimeContext.getCompiledGraph().getState(runnableConfig);
            OverAllState state = stateSnapshot.state();
            state.withResume();

            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createFeedBack(agentRuntimeContext), runnableConfig);

        }

        return agentEvent;

    }

}
