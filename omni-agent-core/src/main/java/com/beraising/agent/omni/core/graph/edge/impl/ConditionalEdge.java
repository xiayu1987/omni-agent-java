package com.beraising.agent.omni.core.graph.edge.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.edge.GraphEdgeBase;
import com.beraising.agent.omni.core.graph.edge.IConditionalEdge;
import com.beraising.agent.omni.core.graph.state.IGraphState;
/**
 * 条件边类，继承自GraphEdgeBase并实现IConditionalEdge接口
 * 用于处理基于条件的图边逻辑
 *
 * @param <T> 图状态类型，必须实现IGraphState接口
 */
public class ConditionalEdge<T extends IGraphState> extends GraphEdgeBase<T> implements IConditionalEdge {

    /**
     * 条件边处理器，用于实际处理条件边的业务逻辑
     */
    private final IConditionalEdgeHandle<T> handle;

    /**
     * 构造函数，创建一个条件边实例
     *
     * @param name 边的名称
     * @param graph 所属的代理图
     * @param handle 条件边处理器
     */
    public ConditionalEdge(String name, IAgentGraph graph, IConditionalEdgeHandle<T> handle) {
        super(name, graph);
        this.handle = handle;
    }

    /**
     * 应用条件边逻辑
     *
     * @param graphState 当前图状态
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentEvent 代理事件
     * @return 处理结果字符串
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public String apply(T graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent)
            throws Exception {
        return handle.handle(graphState, agentRuntimeContext, agentEvent);
    }

    /**
     * 条件边处理器接口
     * 定义了处理条件边逻辑的方法规范
     *
     * @param <T> 图状态类型，必须实现IGraphState接口
     */
    public interface IConditionalEdgeHandle<T extends IGraphState> {

        /**
         * 处理条件边逻辑
         *
         * @param graphState 当前图状态
         * @param agentRuntimeContext 代理运行时上下文
         * @param agentEvent 代理事件
         * @return 处理结果字符串
         * @throws Exception 处理过程中可能抛出的异常
         */
        String handle(T graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent)
                throws Exception;

    }

}

