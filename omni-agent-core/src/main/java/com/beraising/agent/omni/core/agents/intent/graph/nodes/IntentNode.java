package com.beraising.agent.omni.core.agents.intent.graph.nodes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;

import com.beraising.agent.omni.core.agents.intent.graph.state.IntentState;
import com.beraising.agent.omni.core.common.JsonUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.exception.BusinessException;
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

        // --- 构造可用 agent 列表（JSON-like，注意对双引号转义） ---
        String agents = getAgentStaticContext()
                .getAgentRegistry()
                .getAllCustomAgents()
                .stream()
                .map(agent -> {
                    String name = JsonUtils.safeForJson(agent.getName());
                    String desc = JsonUtils.safeForJson(agent.getDescription());
                    return "{\"name\":\"" + name + "\",\"description\":\"" + desc + "\"}";
                })
                .collect(Collectors.joining(", "));

        // --- Prompt 各段（不使用 Java 文本块，兼容更老的 Java 版本） ---
        SystemMessage systemMessageIntro = new SystemMessage(
                "你是一个多Agent任务调度器。你的任务是：根据用户对话判断是否继续当前任务或切换到另一个Agent。" +
                        "**必须只输出严格符合资源文件中指定 JSON 格式的纯 JSON，不允许输出任何解释文本或额外说明。**");

        SystemMessage systemMessageAgents = new SystemMessage("当前可用Agents：[" + agents + "]");

        String taskDesc = agentRuntimeContext.getAgent().getDescription();
        SystemMessage systemMessageRule = new SystemMessage("当前任务描述：" + (taskDesc == null ? "" : taskDesc)
                + "\n请根据用户对话判断应由哪个Agent开始或继续任务，并仅返回符合格式的JSON。");

        // 直接把资源文件里的 intentFormat 传给模型（你已经把格式写在资源文件里）
        SystemMessage systemMessageFormat = new SystemMessage(this.intentFormat);

        String userDialog = graphState.getUserInput();
        UserMessage userMessage = new UserMessage("当前任务对话记录：" + (userDialog == null ? "" : userDialog));

        Prompt prompt = new Prompt(List.of(systemMessageIntro, systemMessageAgents,
                systemMessageRule, systemMessageFormat, userMessage));

        String content = getChatClient().prompt(prompt)
                .call().content();

        String jsonStr = JsonUtils.extractFirstJson(content);

        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        boolean isSuccess = jsonObject.get("isSuccess").getAsBoolean();
        String message = jsonObject.get("message").getAsString();
        if (isSuccess == false) {
            throw new BusinessException(message);
        }

        return graphState.getUpdatedIntentResult(content);
    }

}
