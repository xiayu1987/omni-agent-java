package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;

public interface IGraph<T extends IGraphState> {

    void invoke(String userQuery, T graphState) throws Exception;

    void invoke(String userQuery) throws Exception;

    StateGraph getStateGraph() throws Exception;

    T getGraphState();

    void setGraphState(T graphState);

    void setAgent(IAgent agent);

    IAgent getAgent();

}
