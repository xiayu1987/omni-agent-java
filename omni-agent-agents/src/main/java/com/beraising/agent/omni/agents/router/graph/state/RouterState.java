package com.beraising.agent.omni.agents.router.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.GraphStateBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class RouterState extends GraphStateBase {

    public RouterState() {
        super();
    }

    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
        input.put("user_input", agentEvent.getAgentRequest().getText());
    }

    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, int feedBackTimes) {

    }

    public String getUserInput() {
        return getState().value("user_input", "");
    }

    public IUpdatedGraphState<RouterState> getUpdatedUserInput(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("user_input", value);
            return result;
        };
    }

    public String getRouterResult() {
        return getState().value("router_result", "");
    }

    public IUpdatedGraphState<RouterState> getUpdatedRouterResult(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("router_result", value);
            return result;
        };
    }

    public HashMap<String, KeyStrategy> getStateKeys() {

        HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

        keyStrategyHashMap.put("user_input", new ReplaceStrategy());
        keyStrategyHashMap.put("router_result", new ReplaceStrategy());
        return keyStrategyHashMap;

    }

}
