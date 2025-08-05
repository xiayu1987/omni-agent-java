package com.beraising.agent.omni.core.graph;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentRequest;

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
    public void invoke(IAgentRuntimeContext agentRuntimeContext) throws Exception {

        IGraphState graphState = buildGraphState(agentRuntimeContext);

        this.setGraphState(graphState);

        getStateGraph().compile().invoke(graphState.createInput(agentRuntimeContext));
    }

    protected IGraphState buildGraphState(IAgentRuntimeContext agentRuntimeContext)
            throws Exception {

        return getGraphStateBuilder().build(agentRuntimeContext);
    }

    public abstract IGraphStateBuilder<T> getGraphStateBuilder();

}
