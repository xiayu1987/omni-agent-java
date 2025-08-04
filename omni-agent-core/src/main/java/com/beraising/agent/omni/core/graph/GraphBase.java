package com.beraising.agent.omni.core.graph;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.agents.IAgentRequest;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public abstract class GraphBase<T extends IGraphState> implements IGraph {

    private IAgent agent;
    private IGraphState graphState;

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    public IGraphState getGraphState() {
        return graphState;
    }

    public void setGraphState(IGraphState graphState) {
        this.graphState = graphState;
    }

    @Override
    public void invoke(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) throws Exception {

        IGraphState graphState = buildGraphState(agentRequest, agentRuntimeContext);

        this.setGraphState(graphState);

        getStateGraph().compile().invoke(graphState.createInput(agentRequest, agentRuntimeContext));
    }

    protected IGraphState buildGraphState(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext)
            throws Exception {

        return getGraphStateBuilder().build(agentRequest, agentRuntimeContext);
    }

    public abstract IGraphStateBuilder<T> getGraphStateBuilder();

}
