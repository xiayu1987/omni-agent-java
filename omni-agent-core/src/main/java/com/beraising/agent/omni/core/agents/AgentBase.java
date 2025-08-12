package com.beraising.agent.omni.core.agents;

import java.util.Map;
import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.IAgentGraphListener;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public abstract class AgentBase implements IAgent {

    private IEventListener eventListener;

    @Override
    public void init(IEventListener eventListener) throws Exception {
        this.eventListener = eventListener;
        getAgentGraph().init(this, eventListener, new AgentGraphListener(new AgentGraphLifecycleListener()));
    }

    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) {

        IAgentRuntimeContext runtimeContext = null;

        try {

            IAgentGraph agentGraph = getAgentGraph();

            runtimeContext = this.eventListener.beforeAgentInvoke(this, agentEvent,
                    getAgentGraph());

            return agentGraph.invoke(runtimeContext);

        } catch (Exception e) {
            e.printStackTrace();
            this.eventListener.onError(this, agentEvent, runtimeContext, e);
        }

        return agentEvent;
    }

    @Override
    public FunctionToolCallback<AsToolRequest, AsToolResponse> asToolCallback(IAgentEvent agentEvent) {
        return FunctionToolCallback.builder(
                getName(), new Function<AsToolRequest, AsToolResponse>() {
                    @Override
                    public AsToolResponse apply(AsToolRequest request) {
                        IAgentEvent agentEvent = null;
                        try {
                            agentEvent = getAgentStaticContext().getAgentEngine().invoke(AgentBase.this, agentEvent);
                        } catch (Exception e) {

                            return AsToolResponse.builder().isSuccess(false).errorMessage("表单未处理成功: " + e.getMessage())
                                    .build();
                        }
                        return AsToolResponse.builder().isSuccess(true).agentResponse(agentEvent.getAgentResponse())
                                .build();
                    }
                })
                .description(getDescription())
                .inputType(AsToolRequest.class)
                .build();
    }

    public class AgentGraphListener implements IAgentGraphListener {

        private GraphLifecycleListener stateGraphLifecycleListener;

        public AgentGraphListener(GraphLifecycleListener stateGraphLifecycleListener) {
            super();
            this.stateGraphLifecycleListener = stateGraphLifecycleListener;
        }

        @Override
        public GraphLifecycleListener getStateGraphLifecycleListener() {
            return this.stateGraphLifecycleListener;
        }

        @Override
        public void setStateGraphLifecycleListener(GraphLifecycleListener stateGraphLifecycleListener) {
            this.stateGraphLifecycleListener = stateGraphLifecycleListener;
        }

        @Override
        public void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IGraphNode graphNode, IAgentResponse agentResponse) throws Exception {
            AgentBase.this.eventListener.onInterrupt(AgentBase.this, agentEvent, agentRuntimeContext,
                    AgentBase.this.getAgentGraph().createOutput(agentRuntimeContext,
                            agentEvent, graphNode));
        }

    }

    public class AgentGraphLifecycleListener implements GraphLifecycleListener {

        @Override
        public void onComplete(String nodeId, Map<String, Object> state) {
            if (nodeId.equals(StateGraph.END)) {
                IAgentEvent agentEvent = null;
                IAgentRuntimeContext runtimeContext = null;

                try {
                    IAgentRuntimeContext agentRuntimeContext = AgentBase.this.getAgentStaticContext()
                            .getAgentSessionManage()
                            .getAgentRuntimeContextById(
                                    state.get(IGraphState.getAgentRuntimeContextIDKey()).toString());

                    agentEvent = ListUtils.lastOf(agentRuntimeContext.getAgentEvents());

                    AgentBase.this.eventListener.onComplete(AgentBase.this, agentEvent, agentRuntimeContext,
                            AgentBase.this.getAgentGraph().createOutput(agentRuntimeContext,
                                    agentEvent, null));

                } catch (Exception e) {

                    e.printStackTrace();
                    AgentBase.this.eventListener.onError(AgentBase.this, agentEvent, runtimeContext, e);
                }
            }
        }
    }
}
