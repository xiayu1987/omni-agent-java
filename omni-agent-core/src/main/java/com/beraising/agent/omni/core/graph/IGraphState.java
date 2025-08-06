package com.beraising.agent.omni.core.graph;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.alibaba.cloud.ai.graph.KeyStrategy;

public interface IGraphState {

    OverAllState getState();

    void setState(OverAllState state);

    HashMap<String, KeyStrategy> getStateKeys();

    default String getAgentSessionID() {
        return getState().value(IGraphState.getAgentSessionIDKey(), "");
    }

    default Map<String, Object> createInput(IAgentRuntimeContext agentRuntimeContext) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(IGraphState.getAgentSessionIDKey(), agentRuntimeContext.getAgentSessionID());
        putInput(inputMap, agentRuntimeContext, ListUtils.lastOf(agentRuntimeContext.getAgentEvents()));
        return inputMap;
    }

    default Map<String, Object> createFeedBack(IAgentRuntimeContext agentRuntimeContext) {
        Map<String, Object> inputMap = new HashMap<>();
        putFeedBack(inputMap, agentRuntimeContext, ListUtils.lastOf(agentRuntimeContext.getAgentEvents()),
                agentRuntimeContext.getAgentEvents().size());
        return inputMap;
    }

    void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent);

    void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, int feedBackTimes);

    static HashMap<String, KeyStrategy> getDefaultStateKeys() {
        HashMap<String, KeyStrategy> stateKeys = new HashMap<>();
        stateKeys.put(IGraphState.getAgentSessionIDKey(), new ReplaceStrategy());
        return stateKeys;
    }

    static String getAgentSessionIDKey() {
        return "session_id";
    }
}
