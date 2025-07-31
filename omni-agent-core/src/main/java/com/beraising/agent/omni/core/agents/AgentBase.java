package com.beraising.agent.omni.core.agents;

import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

public abstract class AgentBase implements IAgent {

    private IAgentListener agentListener;

    @Override
    public void init(IAgentListener agentListener) throws Exception {
        this.agentListener = agentListener;
        getGraph().setAgent(this);
        getGraph().getStateGraph().compile();
    }

    @Override
    public void invoke(IAgentRuntimeContext agentRuntimeContext) {
        try {
            agentListener.beforeInvoke();
            getGraph().invoke(null);
            agentListener.afterInvoke();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback() {
        return FunctionToolCallback.builder(
                getName(), new Function<AsToolRequest, AsToolResponse>() {
                    @Override
                    public AsToolResponse apply(AsToolRequest request) {
                        return null;
                    }
                })
                .description(getDescription())
                .inputType(AsToolRequest.class)
                .build();
    }

}
