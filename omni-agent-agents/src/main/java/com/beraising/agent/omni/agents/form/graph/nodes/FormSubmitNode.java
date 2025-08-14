package com.beraising.agent.omni.agents.form.graph.nodes;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.core.io.Resource;

import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.agents.form.graph.state.FormSubmitData;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
import com.google.gson.Gson;

public class FormSubmitNode extends GraphNodeBase<FormState> {

    private ToolCallbackProvider formTools;
    private Resource formSubmitFormat;

    public FormSubmitNode(String name, IAgentGraph graph, ToolCallbackProvider formTools, Resource formSubmitFormat) {
        super(name, graph);
        this.formTools = formTools;
        this.formSubmitFormat = formSubmitFormat;
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        SystemMessage systemMessageNotFound = new SystemMessage("未找到工具输出错误");
        SystemMessage systemMessageFormat = new SystemMessage(this.formSubmitFormat);
        SystemMessage systemMessageStep = new SystemMessage(getName());
        UserMessage userMessage = new UserMessage(graphState.getFormDataFeedback());
        Prompt prompt = new Prompt(List.of(
                userMessage, systemMessageStep, systemMessageFormat, systemMessageNotFound));

        String content = getChatClient().prompt(prompt)
                .toolCallbacks(this.formTools).call().content();

        String jsonStr = content.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1");

        return graphState.getUpdatedFormSubmitResult(new Gson().fromJson(jsonStr, FormSubmitData.class));
    }

}
