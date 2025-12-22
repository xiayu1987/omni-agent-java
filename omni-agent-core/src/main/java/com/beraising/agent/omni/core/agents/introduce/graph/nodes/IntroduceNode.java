package com.beraising.agent.omni.core.agents.introduce.graph.nodes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import com.beraising.agent.omni.core.agents.introduce.graph.state.IntroduceState;
import com.beraising.agent.omni.core.common.JsonUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

public class IntroduceNode extends GraphNodeBase<IntroduceState> {

	public IntroduceNode(String name, IAgentGraph graph) {
		super(name, graph);
	}

	@Override
	public IUpdatedGraphState<IntroduceState> apply(IntroduceState graphState, IAgentRuntimeContext agentRuntimeContext,
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
				"你是一个功能介绍当前可用Agents所具备的所有功能。");

		SystemMessage systemMessageAgents = new SystemMessage("当前可用Agents：[" + agents + "]");

		String taskDesc = agentRuntimeContext.getAgent().getDescription();
		SystemMessage systemMessageRule = new SystemMessage("当前任务描述：" + (taskDesc == null ? "" : taskDesc)
				+ "\n请根据用户对话介绍当前可用Agents所具备的所有功能。");

		String userDialog = graphState.getUserInput();
		UserMessage userMessage = new UserMessage("当前任务对话记录：" + (userDialog == null ? "" : userDialog));

		Prompt prompt = new Prompt(List.of(systemMessageIntro, systemMessageAgents,
				systemMessageRule, userMessage));

		String content = getChatClient().prompt(prompt)
				.call().content();

		return graphState.getUpdatedIntroduceResult(content);
	}

}
