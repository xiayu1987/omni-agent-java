package com.beraising.agent.omni.core.agents;

import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.event.IAgentRequest;

public abstract class AgentBase implements IAgent {

    private IEventListener agentListener;

    @Override
    public void init(IEventListener agentListener) throws Exception {
        this.agentListener = agentListener;
        getGraph().setAgent(this);
        getGraph().getStateGraph().compile();
    }

    @Override
    public void invoke(IAgentEvent agentEvent) {
        try {
            agentListener.beforeInvoke(this, agentEvent);

            IAgentRuntimeContext runtimeContext = getAgentRuntimeContextBuilder().build(agentEvent);

            getGraph().invoke(runtimeContext);

            agentListener.afterInvoke(this, agentEvent, runtimeContext);

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
                        try {
                            getAgentStaticContext().getAgentEngine().invoke(AgentBase.this, null);
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
