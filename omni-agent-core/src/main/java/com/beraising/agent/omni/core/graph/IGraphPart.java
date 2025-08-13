package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;

public interface IGraphPart {

    String getName();

    IAgentGraph getGraph();

    StateInfo getGraphState(OverAllState state) throws Exception;

    class StateInfo {
        private IGraphState graphState;
        private IAgentRuntimeContext agentRuntimeContext;
        private IAgentEvent agentEvent;

        public StateInfo(IGraphState graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
            this.graphState = graphState;
            this.agentRuntimeContext = agentRuntimeContext;
            this.agentEvent = agentEvent;
        }

        public IGraphState getGraphState() {
            return graphState;
        }

        public IAgentRuntimeContext getAgentRuntimeContext() {
            return agentRuntimeContext;
        }

        public IAgentEvent getAgentEvent() {
            return agentEvent;
        }
    }
}
