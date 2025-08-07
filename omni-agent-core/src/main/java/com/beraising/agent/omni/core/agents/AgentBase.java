package com.beraising.agent.omni.core.agents;

import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;

public abstract class AgentBase implements IAgent {

    @Override
    public void init() throws Exception {
        getGraph().setAgent(this);
    }

    @Override
    public void invoke(IAgentEvent agentEvent, IEventListener eventListener) {
        try {

            IAgentRuntimeContext runtimeContext = getAgentRuntimeContextBuilder().build(this, agentEvent);

            eventListener.beforeAgentInvoke(this, agentEvent, runtimeContext);

            getGraph().invoke(runtimeContext, eventListener);

            eventListener.afterAgentInvoke(this, agentEvent, runtimeContext);

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
                            // getAgentStaticContext().getAgentEngine().invoke(AgentBase.this, agentEvent);
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
