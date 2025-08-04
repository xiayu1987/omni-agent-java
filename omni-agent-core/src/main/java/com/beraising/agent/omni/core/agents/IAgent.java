package com.beraising.agent.omni.core.agents;

import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IGraph;

import lombok.Data;

public interface IAgent {

    String getName();

    String getDescription();

    IAgentStaticContext getAgentStaticContext();

    IGraph getGraph();

    void init(IAgentListener agentListener) throws Exception;

    void invoke(IAgentRequest agentRequest);

    void invoke(IAgentRequest agentRequest, IAgentRuntimeContext agentSession);

    FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback();

    @Data
    public class AsToolRequest {
        private String sessionID;
    }

    @Data
    public class AsToolResponse {
        private String sessionID;
    }
}
