package com.beraising.agent.omni.core.graph.state;

import java.util.HashMap;

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

    static HashMap<String, KeyStrategy> getDefaultStateKeys() {
        HashMap<String, KeyStrategy> stateKeys = new HashMap<>();
        stateKeys.put(IGraphState.getAgentSessionIDKey(), new ReplaceStrategy());
        return stateKeys;
    }

    static String getAgentSessionIDKey() {
        return "session_id";
    }
}
