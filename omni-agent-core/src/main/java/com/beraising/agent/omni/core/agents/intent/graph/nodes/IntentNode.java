package com.beraising.agent.omni.core.agents.intent.graph.nodes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;

import com.beraising.agent.omni.core.agents.intent.graph.state.IntentState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class IntentNode extends GraphNodeBase<IntentState> {

    private Resource intentFormat;

    public IntentNode(String name, IAgentGraph graph, Resource intentFormat) {
        super(name, graph);
        this.intentFormat = intentFormat;
    }

    @Override
    public IUpdatedGraphState<IntentState> apply(IntentState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        String agents = getAgentStaticContext().getAgentRegistry().getAllCustomAgents().stream().map((agent) -> {
            return "{agent:" + agent.getName() + ": " + "description:" + agent.getDescription() + "}";
        }).collect(Collectors.joining(","));

        SystemMessage systemMessageAgents = new SystemMessage("\r\n当前任务可用agent" + agents);
        SystemMessage systemMessageRule = new SystemMessage(
                "\r\n当前任务：" + agentRuntimeContext.getAgent().getDescription());
        SystemMessage systemMessageFormat = new SystemMessage(this.intentFormat);
        UserMessage userMessage = new UserMessage("\r\n当前任务对话记录：" + graphState.getUserInput());

        Prompt prompt = new Prompt(List.of(systemMessageAgents, systemMessageRule, systemMessageFormat,
                userMessage));

        String content = getChatClient().prompt(prompt)
                .call().content();

        String jsonStr = content.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1");

        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        boolean isSuccess = jsonObject.get("isSuccess").getAsBoolean();
        if (isSuccess == false) {
            throw new Exception(jsonStr);
        }

        return graphState.getUpdatedIntentResult(content);
    }

}
