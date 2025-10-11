package com.beraising.agent.omni.agents.form.graph.nodes;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.core.io.Resource;

import com.beraising.agent.omni.agents.form.graph.state.FormState;
import com.beraising.agent.omni.core.common.JsonUtils;
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
    public IUpdatedGraphState<FormState> apply(FormState graphState,
            IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        // 规则约束
        SystemMessage systemMessageRule = new SystemMessage(
                "严格要求：\n" +
                        "1. 每次必须重新获取表单信息\n" +
                        "2. 获取失败时必须返回错误信息（isSuccess=false, message=错误原因）\n" +
                        "3. 只允许通过工具获取结果，不要输出额外解释或文本");

        // 任务说明
        SystemMessage systemMessageTask = new SystemMessage(
                "任务：获取表单结构数据，并返回 JSON 格式的表单信息");

        // 输出格式
        SystemMessage systemMessageFormat = new SystemMessage(
                "输出格式说明：\n" + this.formFormat);

        // 用户输入
        UserMessage userMessage = new UserMessage(
                "用户请求内容如下，请基于此获取表单结构：\n" + graphState.getUserInput());

        // 构造 Prompt （顺序：任务 -> 格式 -> 规则 -> 用户输入）
        Prompt prompt = new Prompt(List.of(
                systemMessageTask,
                systemMessageFormat,
                systemMessageRule,
                userMessage));

        // 调用模型
        String content = getChatClient()
                .prompt(prompt)
                .toolCallbacks(this.formTools)
                .call()
                .content();

        // 提取 JSON
        String jsonStr = JsonUtils.extractFirstJson(content);

        // 校验 JSON 结果
        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        boolean isSuccess = jsonObject.has("isSuccess") && jsonObject.get("isSuccess").getAsBoolean();

        if (!isSuccess) {
            String errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
            throw new Exception("表单获取失败: " + errorMessage);
        }

        // 更新状态
        return graphState.getUpdatedFormGetResult(jsonStr);
    }

}
