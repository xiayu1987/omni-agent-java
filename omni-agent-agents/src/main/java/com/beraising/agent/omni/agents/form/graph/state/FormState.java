package com.beraising.agent.omni.agents.form.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.GraphStateBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphStateValue;
import com.google.gson.Gson;

public class FormState extends GraphStateBase {

    public FormState() {
        super();
    }

    public void putUserInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        input.put("user_input", agentEvent.getAgentRequest().getRequestData());
    }

    public void putFormDataFeedback(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        feedBack.put("form_data_feedback", agentEvent.getAgentRequest().getRequestData());
    }

    public String getUserInput() {
        return getState().value("user_input", "");
    }

    public String getFormDataFeedback() {
        return getState().value("form_data_feedback", "");
    }

    public String getFormGetResult() {
        return getState().value("form_get_result", "");
    }

    public FormSubmitData getFormSubmitResult() {

        return new Gson().fromJson(getState().value("form_submit_result", "{}"),
                FormSubmitData.class);
    }

    public IUpdatedGraphStateValue<FormState> getUpdatedUserInput(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("user_input", value);
            return result;
        };
    }

    public IUpdatedGraphStateValue<FormState> getUpdatedFormGetResult(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("form_get_result", value);
            return result;
        };
    }

    public IUpdatedGraphStateValue<FormState> getUpdatedFormSubmitResult(FormSubmitData value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();

            result.put("form_submit_result",
                    new Gson().toJson(value));

            return result;
        };
    }

    @Override
    public HashMap<String, KeyStrategy> getStateKeys() {

        HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

        keyStrategyHashMap.put("user_input", new ReplaceStrategy());
        keyStrategyHashMap.put("form_get_result", new ReplaceStrategy());
        keyStrategyHashMap.put("form_submit_result", new ReplaceStrategy());
        keyStrategyHashMap.put("form_data_feedback", new ReplaceStrategy());
        return keyStrategyHashMap;

    }

}
