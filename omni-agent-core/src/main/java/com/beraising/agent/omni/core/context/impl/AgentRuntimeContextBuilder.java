package com.beraising.agent.omni.core.context.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.IAgentRuntimeContextBuilder;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.IInterruptNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
/**
 * AgentRuntimeContextBuilder 是一个用于构建和初始化 Agent 运行时上下文（AgentRuntimeContext）的组件。
 * 它实现了 IAgentRuntimeContextBuilder 接口，提供了三种主要方法：initialize、enrich 和 build，
 * 分别用于初始化上下文、丰富上下文信息以及完整地构建运行时上下文对象。
 */
@Component
public class AgentRuntimeContextBuilder implements IAgentRuntimeContextBuilder {

    /**
     * 默认构造函数。
     */
    public AgentRuntimeContextBuilder() {
    }

    /**
     * 初始化一个新的 AgentRuntimeContext 实例，并填充基础字段。
     *
     * @param agentEvent 代理事件对象，不能为 null
     * @return 初始化后的 IAgentRuntimeContext 对象
     * @throws Exception 如果传入的 agentEvent 为 null，则抛出 IllegalArgumentException
     */
    @Override
    public IAgentRuntimeContext initialize(IAgentEvent agentEvent) throws Exception {
        if (agentEvent == null) {
            throw new IllegalArgumentException("Agent event cannot be null");
        }

        IAgentRuntimeContext agentRuntimeContext = new AgentRuntimeContext();
        agentRuntimeContext.setAgentRuntimeContextId(UUID.randomUUID().toString());
        agentRuntimeContext.setAgentSessionId(agentEvent.getAgentSessionId());
        agentRuntimeContext.setAgent(null);
        agentRuntimeContext.setAgentName("");
        agentRuntimeContext.setIsEnd(false);
        agentRuntimeContext.getAgentEvents().add(agentEvent);

        return agentRuntimeContext;
    }

    /**
     * 使用给定的图结构（graph）来丰富已有的 AgentRuntimeContext 上下文。
     * 包括设置代理实例、状态图、编译配置等关键属性。
     *
     * @param agentRuntimeContext 要被丰富的运行时上下文对象，不能为 null
     * @param graph               图结构定义，包含节点与边的信息，不能为 null
     * @return 经过增强处理后的 IAgentRuntimeContext 对象
     * @throws Exception 若任一输入参数为 null 则抛出 IllegalArgumentException；其他异常可能由内部逻辑引发
     */
    @Override
    public IAgentRuntimeContext enrich(IAgentRuntimeContext agentRuntimeContext, IAgentGraph graph) throws Exception {
        if (agentRuntimeContext == null) {
            throw new IllegalArgumentException("Agent runtime context cannot be null");
        }

        if (graph == null) {
            throw new IllegalArgumentException("Agent graph cannot be null");
        }

        // 设置代理及其名称
        agentRuntimeContext.setAgent(graph.getAgent());
        if (graph.getAgent() != null) {
            agentRuntimeContext.setAgentName(graph.getAgent().getName());
        }

        // 创建并设置图的状态管理器
        IGraphState graphState = graph.newGraphState();
        agentRuntimeContext.setGraphState(graphState);

        HashMap<String, KeyStrategy> stateKeys = graphState.getStateKeys();

        // 构造键策略工厂，合并默认键与自定义键
        KeyStrategyFactory keyStrategyFactory = () -> {

            HashMap<String, KeyStrategy> result = new HashMap<>();
            result.putAll(IGraphState.getDefaultStateKeys());
            result.putAll(stateKeys);
            return result;
        };

        // 清空旧节点和边数据，重新获取新的状态图表示
        graph.setGraphNodes(new ArrayList<>());
        graph.setGraphEdges(new ArrayList<>());
        StateGraph stateGraph = graph.getStateGraph(keyStrategyFactory);

        // 提取所有中断类型的节点名作为中断点列表
        String[] interruptNodes = graph.getGraphNodes().stream()
                .filter(node -> node instanceof IInterruptNode)
                .map(node -> node.getName())
                .toArray(String[]::new);

        // 编译最终的状态图并保存到上下文中
        CompiledGraph compiledGraph = stateGraph
                .compile(CompileConfig.builder()
                        .saverConfig(graph.getAgent().getAgentStaticContext().getGraphSaverConfig())
                        .withLifecycleListener(graph.getAgentGraphListener().getStateGraphLifecycleListener())
                        .interruptAfter(interruptNodes)
                        .build());
        agentRuntimeContext.setCompiledGraph(compiledGraph);

        return agentRuntimeContext;
    }

    /**
     * 根据提供的代理事件和图结构完整地构建一个 AgentRuntimeContext 实例。
     * 此操作综合了 initialize 和 enrich 的功能，适用于一次性完成上下文创建的场景。
     *
     * @param agentEvent 代理触发事件，必须提供且其 sessionId 不可为空
     * @param graph      图结构描述，用于构建状态流转关系
     * @return 已完全构建好的 IAgentRuntimeContext 对象
     * @throws Exception 若参数非法或构建过程中发生错误则抛出相应异常
     */
    @Override
    public IAgentRuntimeContext build(IAgentEvent agentEvent, IAgentGraph graph)
            throws Exception {
        if (agentEvent == null) {
            throw new IllegalArgumentException("Agent event cannot be null");
        }
        if (agentEvent.getAgentSessionId() == null) {
            throw new IllegalArgumentException("Agent session ID cannot be null");
        }

        IAgentRuntimeContext agentRuntimeContext = new AgentRuntimeContext();
        agentRuntimeContext.setAgentRuntimeContextId(UUID.randomUUID().toString());
        agentRuntimeContext.setAgentSessionId(agentEvent.getAgentSessionId());
        agentRuntimeContext.setAgent(graph.getAgent());
        if (graph.getAgent() != null) {
            agentRuntimeContext.setAgentName(graph.getAgent().getName());
        }
        agentRuntimeContext.setIsEnd(false);

        // 创建并设置图的状态管理器
        IGraphState graphState = graph.newGraphState();
        agentRuntimeContext.setGraphState(graphState);

        HashMap<String, KeyStrategy> stateKeys = graphState.getStateKeys();

        // 构造键策略工厂，合并默认键与自定义键
        KeyStrategyFactory keyStrategyFactory = () -> {

            HashMap<String, KeyStrategy> result = new HashMap<>();
            result.putAll(IGraphState.getDefaultStateKeys());
            result.putAll(stateKeys);
            return result;
        };

        // 清空旧节点和边数据，重新获取新的状态图表示
        graph.setGraphNodes(new ArrayList<>());
        graph.setGraphEdges(new ArrayList<>());
        StateGraph stateGraph = graph.getStateGraph(keyStrategyFactory);

        // 提取所有中断类型的节点名作为中断点列表
        String[] interruptNodes = graph.getGraphNodes().stream()
                .filter(node -> node instanceof IInterruptNode)
                .map(node -> node.getName())
                .toArray(String[]::new);

        // 编译最终的状态图并保存到上下文中
        CompiledGraph compiledGraph = stateGraph
                .compile(CompileConfig.builder()
                        .saverConfig(graph.getAgent().getAgentStaticContext().getGraphSaverConfig())
                        .withLifecycleListener(graph.getAgentGraphListener().getStateGraphLifecycleListener())
                        .interruptAfter(interruptNodes)
                        .build());
        agentRuntimeContext.setCompiledGraph(compiledGraph);

        agentRuntimeContext.getAgentEvents().add(agentEvent);

        return agentRuntimeContext;
    }

}

