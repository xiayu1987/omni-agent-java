package com.beraising.agent.omni.core.graph.node.impl;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.node.GraphNodeBase;
import com.beraising.agent.omni.core.graph.node.IInterruptNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;

/**
 * InterruptNode类表示一个中断节点，用于处理图状态中的中断事件。
 * 该节点继承自GraphNodeBase并实现了IInterruptNode接口。
 *
 * @param <T> 图状态类型，必须实现IGraphState接口
 */
public class InterruptNode<T extends IGraphState> extends GraphNodeBase<T> implements IInterruptNode {

    /**
     * 构造函数，创建一个新的中断节点实例。
     *
     * @param name  节点名称
     * @param graph 所属的代理图
     */
    public InterruptNode(String name, IAgentGraph graph) {
        super(name, graph);
    }

    /**
     * 应用中断节点逻辑，处理传入的图状态和事件。
     * 该方法会触发代理图监听器的中断回调，并返回更新后的图状态。
     *
     * @param graphState          当前图状态
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentEvent          代理事件
     * @return 更新后的图状态，包含新的会话ID
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public IUpdatedGraphState<T> apply(T graphState,
            IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) throws Exception {
        // 触发代理图监听器的中断事件回调
        getGraph().getAgentGraphListener().onInterrupt(getGraph().getAgent(), agentEvent, agentRuntimeContext, this,
                null);

        // 返回包含新会话ID的更新图状态
        return graphState.getUpdatedSessionID(agentEvent.getAgentSessionId());
    }
}
