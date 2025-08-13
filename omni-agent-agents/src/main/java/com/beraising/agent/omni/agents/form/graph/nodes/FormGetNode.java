package com.beraising.agent.omni.agents.form.graph.nodes;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;

import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class FormGetNode extends GraphNodeBase<FormState> {

    private ToolCallbackProvider formTools;

    public FormGetNode(String name, IAgentGraph graph, ToolCallbackProvider formTools) {
        super(name, graph);
        this.formTools = formTools;
    }

    @Override
    public IUpdatedGraphState<FormState> apply(FormState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        SystemMessage systemMessageFormat = new SystemMessage("返回单据json格式");
        SystemMessage systemMessageStep = new SystemMessage(getName());
        UserMessage userMessage = new UserMessage(graphState.getUserInput());
        Prompt prompt = new Prompt(List.of(
                userMessage,
                systemMessageFormat,
                systemMessageStep));

        String content = getChatClient().prompt(prompt)
                .toolCallbacks(this.formTools).call().content();

        String jsonStr = content.replaceAll("(?s)```json\\s*(.*?)\\s*```", "$1");

        return graphState.getUpdatedFormResult(jsonStr);
    }

}
