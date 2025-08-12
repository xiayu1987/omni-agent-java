package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.graph.node.IGraphNode;

public interface IAgentGraphListener {

    void setStateGraphLifecycleListener(GraphLifecycleListener stateGraphLifecycleListener);

    GraphLifecycleListener getStateGraphLifecycleListener();

    void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
            IGraphNode graphNode,
            IAgentResponse agentResponse)
            throws Exception;

}
