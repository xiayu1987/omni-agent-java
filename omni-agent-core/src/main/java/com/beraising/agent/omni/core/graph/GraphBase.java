package com.beraising.agent.omni.core.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.agents.IAgent;

public abstract class GraphBase implements IGraph {

    private IAgent agent;

    @Override
    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    @Override
    public IAgent getAgent() {
        return agent;
    }

    @Override
    public Optional<OverAllState> invoke(Map<String, Object> inputs) throws Exception {

        inputs = new HashMap<>() {
            {
                put("user_query", "我明天请假");
            }
        };

        return getStateGraph().compile().invoke(inputs);
    }

}
