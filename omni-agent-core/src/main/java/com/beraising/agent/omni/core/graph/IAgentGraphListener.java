package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.graph.node.IGraphNode;

/**
 * IAgentGraphListener接口定义了代理图监听器的行为规范。
 * 该接口用于监听和处理代理图生命周期中的各种事件。
 */
public interface IAgentGraphListener {

    /**
     * 设置状态图生命周期监听器
     *
     * @param stateGraphLifecycleListener 状态图生命周期监听器实例
     */
    void setStateGraphLifecycleListener(GraphLifecycleListener stateGraphLifecycleListener);

    /**
     * 获取状态图生命周期监听器
     *
     * @return 当前设置的状态图生命周期监听器实例
     */
    GraphLifecycleListener getStateGraphLifecycleListener();

    /**
     * 当代理执行被中断时触发的回调方法
     *
     * @param agent 代理实例
     * @param agentEvent 代理事件
     * @param agentRuntimeContext 代理运行时上下文
     * @param graphNode 图节点
     * @param agentResponse 代理响应
     * @throws Exception 当处理中断过程中发生异常时抛出
     */
    void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
            IGraphNode graphNode,
            IAgentResponse agentResponse)
            throws Exception;

}
