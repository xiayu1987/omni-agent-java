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
/**
 * IntentNode 类表示一个意图识别节点，用于在 Agent 图中处理用户的输入并决定是否需要切换到其他 Agent。
 * 它继承自 GraphNodeBase 并重写了 apply 方法以实现具体的意图判断逻辑。
 */
public class IntentNode extends GraphNodeBase<IntentState> {

    /**
     * 意图格式资源，定义了模型输出的 JSON 格式要求。
     */
    private Resource intentFormat;

    /**
     * 构造方法，初始化 IntentNode 实例。
     *
     * @param name         节点名称
     * @param graph        所属的 Agent 图
     * @param intentFormat 意图格式资源对象，用于指导模型输出结构
     */
    public IntentNode(String name, IAgentGraph graph, Resource intentFormat) {
        super(name, graph);
        this.intentFormat = intentFormat;
    }

    /**
     * 应用当前节点逻辑，基于用户输入和上下文状态进行意图分析，并返回更新后的图状态。
     *
     * @param graphState           当前图的状态信息
     * @param agentRuntimeContext  运行时上下文，提供当前 Agent 和环境信息
     * @param agentEvent           触发该操作的事件
     * @return                     更新后的图状态结果
     * @throws Exception           如果意图解析失败或业务异常则抛出异常
     */
    @Override
    public IUpdatedGraphState<IntentState> apply(IntentState graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception {

        // 构建可用的自定义 Agent 列表，转换为 JSON 字符串形式供提示词使用
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

        // 构造系统消息部分：介绍角色、规则与格式要求
        SystemMessage systemMessageIntro = new SystemMessage(
                "你是一个多Agent任务调度器。你的任务是：根据用户对话判断是否继续当前任务或切换到另一个Agent。" +
                        "**必须只输出严格符合资源文件中指定 JSON 格式的纯 JSON，不允许输出任何解释文本或额外说明。**");

        SystemMessage systemMessageAgents = new SystemMessage("当前可用Agents：[" + agents + "]");

        String taskDesc = agentRuntimeContext.getAgent().getDescription();
        SystemMessage systemMessageRule = new SystemMessage("当前任务描述：" + (taskDesc == null ? "" : taskDesc)
                + "\n请根据用户对话判断应由哪个Agent开始或继续任务，并仅返回符合格式的JSON。");

        // 使用预设的意图格式作为系统指令的一部分传递给模型
        SystemMessage systemMessageFormat = new SystemMessage(this.intentFormat);

        // 用户输入内容封装成用户消息
        String userDialog = graphState.getUserInput();
        UserMessage userMessage = new UserMessage("当前任务对话记录：" + (userDialog == null ? "" : userDialog));

        // 组装完整的提示词
        Prompt prompt = new Prompt(List.of(systemMessageIntro, systemMessageAgents,
                systemMessageRule, systemMessageFormat, userMessage));

        // 发送提示词请求并获取响应内容
        String content = getChatClient().prompt(prompt)
                .call().content();

        // 提取第一个有效的 JSON 内容
        String jsonStr = JsonUtils.extractFirstJson(content);

        // 解析 JSON 响应并检查 isSuccess 字段
        JsonObject jsonObject = new Gson().fromJson(jsonStr, JsonObject.class);
        boolean isSuccess = jsonObject.get("isSuccess").getAsBoolean();
        String message = jsonObject.get("message").getAsString();
        if (isSuccess == false) {
            throw new BusinessException(message);
        }

        // 返回更新后的意图结果状态
        return graphState.getUpdatedIntentResult(content);
    }

}

