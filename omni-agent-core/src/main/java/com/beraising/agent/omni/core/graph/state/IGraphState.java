package com.beraising.agent.omni.core.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategy;

public interface IGraphState {

    OverAllState getState();

    void setState(OverAllState state);

    HashMap<String, KeyStrategy> getStateKeys();

    default String getAgentSessionID() {
        return getState().value(IGraphState.getAgentSessionIDKey(), "");
    }

    default String getAgentRuntimeContextID() {
        return getState().value(IGraphState.getAgentRuntimeContextIDKey(), "");
    }

    default <T extends IGraphState> IUpdatedGraphState<T> getUpdatedSessionID(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put(IGraphState.getAgentSessionIDKey(), value);
            return result;
        };
    }

    static HashMap<String, KeyStrategy> getDefaultStateKeys() {
        HashMap<String, KeyStrategy> stateKeys = new HashMap<>();
        stateKeys.put(IGraphState.getAgentSessionIDKey(), new ReplaceStrategy());
        stateKeys.put(IGraphState.getAgentRuntimeContextIDKey(), new ReplaceStrategy());
        return stateKeys;
    }

    static String getAgentSessionIDKey() {
        return "session_id";
    }

    static String getAgentRuntimeContextIDKey() {
        return "runtime_context_id";
    }
}
