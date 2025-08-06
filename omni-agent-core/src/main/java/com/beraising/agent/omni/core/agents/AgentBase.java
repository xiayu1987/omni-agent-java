package com.beraising.agent.omni.core.agents;

import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;

public abstract class AgentBase implements IAgent {

    private IEventListener agentListener;

    @Override
    public void init(IEventListener agentListener) throws Exception {
        this.agentListener = agentListener;
        getGraph().setAgent(this);
    }

    @Override
    public void invoke(IAgentEvent agentEvent) {
        try {

            IAgentRuntimeContext runtimeContext = getAgentRuntimeContextBuilder().build(this, agentEvent);

            agentListener.beforeInvoke(this, agentEvent, runtimeContext);

            getGraph().invoke(runtimeContext);

            agentListener.afterInvoke(this, agentEvent, runtimeContext);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback(IAgentEvent agentEvent) {
        return FunctionToolCallback.builder(
                getName(), new Function<AsToolRequest, AsToolResponse>() {
                    @Override
                    public AsToolResponse apply(AsToolRequest request) {
                        try {
                            getAgentStaticContext().getAgentEngine().invoke(AgentBase.this, agentEvent);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        return new AsToolResponse();
                    }
                })
                .description(getDescription())
                .inputType(AsToolRequest.class)
                .build();
    }

}
