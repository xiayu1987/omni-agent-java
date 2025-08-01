package com.beraising.agent.omni.core.graph;

import com.beraising.agent.omni.core.agents.IAgent;

public abstract class GraphBase<T extends IGraphState> implements IGraph<T> {

    private IAgent agent;
    private T graphState;

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    public T getGraphState() {
        return graphState;
    }

    public void setGraphState(T graphState) {
        this.graphState = graphState;
    }

    public abstract T newGraphState();

    @Override
    public void invoke(String userQuery) throws Exception {

        T graphState = newGraphState();

        this.setGraphState(graphState);

        getStateGraph().compile().invoke(graphState.createUserQuery(userQuery));
    }

    @Override
    public void invoke(String userQuery, T graphState) throws Exception {

    }

}
