package com.beraising.agent.omni.core.agents;

import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IGraph;

import lombok.Data;

public interface IAgent {

    String getName();

    String getDescription();

    IAgentStaticContext getAgentStaticContext();

    IAgentRuntimeContextBuilder getAgentRuntimeContextBuilder();

    IGraph getGraph();

    void init(IEventListener agentListener) throws Exception;

    void invoke(IAgentEvent agentEvent);

    FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback(IAgentEvent agentEvent);

    @Data
    public class AsToolRequest {
        private String sessionID;
    }

    @Data
    public class AsToolResponse {
        private String sessionID;
    }
}
