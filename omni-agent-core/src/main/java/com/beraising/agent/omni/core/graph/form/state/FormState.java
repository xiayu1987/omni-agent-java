package com.beraising.agent.omni.core.graph.form.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.agents.IAgentRequest;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.GraphStateBase;
import com.beraising.agent.omni.core.graph.IUpdatedGraphState;
import com.beraising.agent.omni.core.graph.router.state.RouterState;

public class FormState extends GraphStateBase {

    public static KeyStrategyFactory getKeyStrategyFactory() {
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

            keyStrategyHashMap.put("user_query", new ReplaceStrategy());
            keyStrategyHashMap.put("router_result", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        return keyStrategyFactory;
    }

    @Override
    public Map<String, Object> createInput(IAgentRequest agentRequest, IAgentRuntimeContext agentRuntimeContext) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("user_query", "");
        return inputMap;
    }

    public String getUserQuery() {
        return getState().value("user_query", "");
    }

    public String getRouterResult() {
        return getState().value("router_result", "");
    }

    public IUpdatedGraphState<RouterState> getUpdatedUserQuery(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("user_query", value);
            return result;
        };
    }

    public IUpdatedGraphState<RouterState> getUpdatedRouterResult(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("router_result", value);
            return result;
        };
    }

}
