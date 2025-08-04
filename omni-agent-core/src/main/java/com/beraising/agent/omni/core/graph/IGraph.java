package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;

public interface IGraph {

    void invoke(String userQuery, IGraphState graphState) throws Exception;

    void invoke(String userQuery) throws Exception;

    StateGraph getStateGraph() throws Exception;

    IGraphState getGraphState();

    void setGraphState(IGraphState graphState);

    void setAgent(IAgent agent);

    IAgent getAgent();

}
