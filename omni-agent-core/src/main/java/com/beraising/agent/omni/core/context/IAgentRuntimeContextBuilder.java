package com.beraising.agent.omni.core.context;

import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;

/**
 * IAgentRuntimeContextBuilder接口定义了代理运行时上下文构建器的功能规范。
 * 该接口提供了初始化、丰富和构建代理运行时上下文的方法。
 */
public interface IAgentRuntimeContextBuilder {

    /**
     * 初始化代理运行时上下文
     *
     * @param agentEvent 代理事件对象，用于初始化上下文的信息源
     * @return 初始化完成的代理运行时上下文对象
     * @throws Exception 初始化过程中可能抛出的异常
     */
    IAgentRuntimeContext initialize(IAgentEvent agentEvent) throws Exception;

    /**
     * 丰富代理运行时上下文
     *
     * @param context 待丰富的代理运行时上下文对象
     * @param graph 代理图对象，提供用于丰富上下文的图结构信息
     * @return 丰富后的代理运行时上下文对象
     * @throws Exception 丰富过程中可能抛出的异常
     */
    IAgentRuntimeContext enrich(IAgentRuntimeContext context, IAgentGraph graph) throws Exception;

    /**
     * 构建代理运行时上下文
     *
     * @param agentEvent 代理事件对象，提供构建上下文所需的事件信息
     * @param graph 代理图对象，提供构建上下文所需的图结构信息
     * @return 构建完成的代理运行时上下文对象
     * @throws Exception 构建过程中可能抛出的异常
     */
    IAgentRuntimeContext build(IAgentEvent agentEvent, IAgentGraph graph) throws Exception;

}

