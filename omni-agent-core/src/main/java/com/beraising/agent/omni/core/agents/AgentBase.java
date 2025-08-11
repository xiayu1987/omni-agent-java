package com.beraising.agent.omni.core.agents;

import java.util.Map;
import java.util.function.Function;
import org.springframework.ai.tool.function.FunctionToolCallback;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.IAgentGraphListener;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.session.IAgentSession;

public abstract class AgentBase implements IAgent {

    private IEventListener eventListener;

    @Override
    public void init(IEventListener eventListener) throws Exception {
        this.eventListener = eventListener;
        getAgentGraph().init(this, eventListener, new AgentGraphListener(new AgentGraphLifecycleListener()));
    }

    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) {
        try {

            IAgentGraph agentGraph = getAgentGraph();

            IAgentRuntimeContext runtimeContext = this.eventListener.beforeAgentInvoke(this, agentEvent,
                    getAgentGraph());

            return agentGraph.invoke(runtimeContext);

        } catch (Exception e) {
            agentEvent.setAgentResponse(null);
        }

        return agentEvent;
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
                            return new AsToolResponse("表单未处理成功: " + e.getMessage());
                        }
                        return new AsToolResponse("已交由表单助手处理");
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

    }

    public class AgentGraphLifecycleListener implements GraphLifecycleListener {

        @Override
        public void onComplete(String nodeId, Map<String, Object> state) {
            if (nodeId.equals(StateGraph.END)) {
                try {
                    IAgentSession agentSession = AgentBase.this.getAgentStaticContext().getAgentSessionManage()
                            .getAgentSessionById(state.get(IGraphState.getAgentSessionIDKey()).toString());

                    AgentBase.this.eventListener.onComplete(AgentBase.this, agentSession);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

}
