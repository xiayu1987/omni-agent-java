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

public abstract class GraphBase<T extends IGraphState> implements IGraph {

    private IAgent agent;
    private List<IGraphNode> graphNodes;

    public GraphBase() {
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
    public void setGraphNodes(List<IGraphNode> graphNodes) {
        this.graphNodes = graphNodes;
    }

    @Override
    public List<IGraphNode> getGraphNodes() {
        return graphNodes;
    }

    @Override
    public void invoke(IAgentRuntimeContext agentRuntimeContext, IEventListener eventListener) throws Exception {

        if (agentRuntimeContext.getAgentEvents().size() == 0) {
            throw new Exception("AgentRuntimeContext has no AgentEvent");
        }

        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(agentRuntimeContext.getAgentSessionID())
                .build();

        if (agentRuntimeContext.getAgentEvents().size() == 1) {
            IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());
            eventListener.beforeGraphInvoke(agent, agentEvent, agentRuntimeContext);

            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createInput(agentRuntimeContext), runnableConfig);
        }

        if (agentRuntimeContext.getAgentEvents().size() > 1) {
            IAgentEvent agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());
            eventListener.beforeGraphInvoke(agent, agentEvent, agentRuntimeContext);

            StateSnapshot stateSnapshot = agentRuntimeContext.getCompiledGraph().getState(runnableConfig);
            OverAllState state = stateSnapshot.state();
            state.withResume();

            agentRuntimeContext.getCompiledGraph()
                    .invoke(agentRuntimeContext.getGraphState().createFeedBack(agentRuntimeContext), runnableConfig);
        }
    }

}
