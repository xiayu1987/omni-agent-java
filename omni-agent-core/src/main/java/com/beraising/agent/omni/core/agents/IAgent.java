package com.beraising.agent.omni.core.agents;

import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.graph.IGraph;
import com.beraising.agent.omni.core.graph.IGraphState;

import lombok.Data;

public interface IAgent {

    String getName();

    String getDescription();

    IAgentStaticContext getAgentStaticContext();

    <T extends IGraphState> IGraph<T> getGraph();

    void init(IAgentListener agentListener) throws Exception;

    void invoke(String userQuery);

    void invoke(String userQuery, IAgentRuntimeContext agentRuntimeContext);

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
