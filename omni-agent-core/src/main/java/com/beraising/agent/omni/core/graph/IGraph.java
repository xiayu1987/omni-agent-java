package com.beraising.agent.omni.core.graph;

import java.util.Map;
import java.util.Optional;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.agents.IAgent;

public interface IGraph {

    Optional<OverAllState> invoke(Map<String, Object> inputs) throws Exception;

    StateGraph getStateGraph() throws Exception;

    void setAgent(IAgent agent);

    IAgent getAgent();

}
