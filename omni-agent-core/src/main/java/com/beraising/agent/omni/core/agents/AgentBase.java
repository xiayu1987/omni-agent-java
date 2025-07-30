package com.beraising.agent.omni.core.agents;

import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.session.IAgentSession;

public abstract class AgentBase implements IAgent {

    @Override
    public void init() throws Exception {
        getGraph().setAgent(this);
        getGraph().getStateGraph().compile();

    }

    @Override
    public IAgentSession invoke(IAgentSession agentSession) {
        try {
            getGraph().invoke(null);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return agentSession;
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
