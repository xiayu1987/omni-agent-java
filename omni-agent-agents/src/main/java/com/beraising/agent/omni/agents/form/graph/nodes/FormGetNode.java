package com.beraising.agent.omni.agents.form.graph.nodes;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.core.io.Resource;

import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class FormGetNode extends GraphNodeBase<FormState> {

    private ToolCallbackProvider formTools;
    private Resource formFormat;

    public FormGetNode(String name, IAgentGraph graph, ToolCallbackProvider formTools, Resource formFormat) {
        super(name, graph);
        this.formTools = formTools;
        this.formFormat = formFormat;
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        SystemMessage systemMessageRule = new SystemMessage("1.每次重新获取表单信息2.获取失败生成错误信息3.只通过工具获取");
        SystemMessage systemMessageStep = new SystemMessage("当前任务获取表单");
        SystemMessage systemMessageFormat = new SystemMessage(this.formFormat);

        UserMessage userMessage = new UserMessage(graphState.getUserInput());
        Prompt prompt = new Prompt(List.of(
                userMessage, systemMessageStep, systemMessageFormat, systemMessageRule));

        String content = getChatClient().prompt(prompt)
                .toolCallbacks(this.formTools).call().content();

        String jsonStr = content.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1");

        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        boolean isSuccess = jsonObject.get("isSuccess").getAsBoolean();
        if (isSuccess == false) {
            throw new Exception(jsonStr);
        }

        return graphState.getUpdatedFormGetResult(jsonStr);
    }

}
