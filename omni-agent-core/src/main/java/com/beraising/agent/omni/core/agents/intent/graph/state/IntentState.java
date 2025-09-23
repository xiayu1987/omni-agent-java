package com.beraising.agent.omni.core.agents.intent.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.GraphStateBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphStateValue;

public class IntentState extends GraphStateBase {

    @Override
    public HashMap<String, KeyStrategy> getStateKeys() {
        HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

        keyStrategyHashMap.put("user_input", new ReplaceStrategy());
        keyStrategyHashMap.put("intent_result", new ReplaceStrategy());
        return keyStrategyHashMap;
    }

    public void putUserInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        input.put("user_input", agentEvent.getAgentRequest().getRequestData());
    }

    public String getUserInput() {
        return getState().value("user_input", "");
    }

    public String getIntentResult() {
        return getState().value("intent_result", "");
    }

    public IUpdatedGraphStateValue<IntentState> getUpdatedIntentResult(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("intent_result", value);
            return result;
        };
    }

}
