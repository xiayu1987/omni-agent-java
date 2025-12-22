package com.beraising.agent.omni.core.agents.intent.graph.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.beraising.agent.omni.core.agents.intent.graph.IIntentGraph;
import com.beraising.agent.omni.core.agents.intent.graph.nodes.IntentNode;
import com.beraising.agent.omni.core.agents.intent.graph.state.IntentState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.impl.AgentResponse;
import com.beraising.agent.omni.core.graph.AgentGraphBase;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
/**
 * 意图识别图组件，用于构建和管理意图识别的状态图。
 * 继承自AgentGraphBase并实现IIntentGraph接口，负责处理用户输入并识别其意图。
 */
@Component
public class IntentGraph extends AgentGraphBase<IntentState> implements IIntentGraph {

    private static final String ROUTER_NODE_NAME = "intent_node";
    private final Resource intentFormat;

    /**
     * 构造函数，初始化意图识别图所需的资源。
     *
     * @param intentFormat 意图格式定义文件资源，通过Spring注入
     */
    public IntentGraph(@Value("classpath:agents-prompts/intent/intent-format.txt") Resource intentFormat) {
        super();
        this.intentFormat = intentFormat;
    }

    /**
     * 获取状态图实例，配置节点和边的关系。
     *
     * @param keyStrategyFactory 键策略工厂，用于创建状态图中的键策略
     * @return 配置完成的状态图对象
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public StateGraph getStateGraph(KeyStrategyFactory keyStrategyFactory) throws Exception {

        // 创建状态图，并添加意图识别节点
        StateGraph stateGraph = new StateGraph(keyStrategyFactory)
                .addNode(ROUTER_NODE_NAME,
                        AsyncNodeAction.node_async(new IntentNode(ROUTER_NODE_NAME, this, intentFormat)))

                .addEdge(StateGraph.START, ROUTER_NODE_NAME)
                .addEdge(ROUTER_NODE_NAME, StateGraph.END);

        return stateGraph;
    }

    /**
     * 创建新的图状态实例。
     *
     * @return 新建的意图状态对象
     */
    @Override
    public IGraphState newGraphState() {
        return new IntentState();
    }

    /**
     * 将用户输入放入图状态中进行处理。
     *
     * @param input               用户输入数据映射
     * @param agentRuntimeContext 运行时上下文信息
     * @param agentEvent          当前代理事件
     * @param graphState          图状态对象，用于存储输入数据
     */
    @Override
    public void putInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IntentState graphState) {
        graphState.putUserInput(input, agentRuntimeContext, agentEvent);
    }

    /**
     * 处理反馈信息（当前为空实现）。
     *
     * @param feedBack            反馈数据映射
     * @param agentRuntimeContext 运行时上下文信息
     * @param agentEvent          当前代理事件
     * @param graphNode           图节点对象
     * @param graphState          图状态对象
     */
    @Override
    public void putFeedBack(Map<String, Object> feedBack, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent, IGraphNode graphNode, IntentState graphState) {

    }

    /**
     * 根据图状态生成输出响应。
     *
     * @param agentRuntimeContext 运行时上下文信息
     * @param agentEvent          当前代理事件
     * @param graphNode           图节点对象，若为null表示最终结果
     * @param graphState          图状态对象，包含处理后的结果
     * @return 代理响应对象，封装了识别到的意图结果
     */
    @Override
    public IAgentResponse createOutput(IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent,
            IGraphNode graphNode, IntentState graphState) {

        // 如果graphNode为null，说明是最终输出，返回意图识别结果
        if (graphNode == null) {
            return AgentResponse.builder().responseType(EAgentResponseType.TEXT)
                    .responseData(graphState.getIntentResult()).build();
        }

        return AgentResponse.builder().build();
    }

}

