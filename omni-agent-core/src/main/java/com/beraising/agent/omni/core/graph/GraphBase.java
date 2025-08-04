package com.beraising.agent.omni.core.graph;

import com.beraising.agent.omni.core.agents.IAgent;

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

    public abstract IGraphState newGraphState();

    @Override
    public void invoke(String userQuery) throws Exception {

        IGraphState graphState = newGraphState();

        this.setGraphState(graphState);

        getStateGraph().compile().invoke(graphState.createUserQuery(userQuery));
    }

    @Override
    public void invoke(String userQuery, IGraphState graphState) throws Exception {

    }

}
