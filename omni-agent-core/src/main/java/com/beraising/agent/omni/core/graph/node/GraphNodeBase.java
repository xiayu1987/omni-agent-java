package com.beraising.agent.omni.core.graph.node;

import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.GraphPartBase;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.state.IGraphState;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphState;
/**
 * 图节点基类，继承自GraphPartBase并实现NodeAction和IGraphNode接口
 * 用于定义图中节点的基本行为和状态处理逻辑
 *
 * @param <T> 图状态类型，必须实现IGraphState接口
 *
 */
public abstract class GraphNodeBase<T extends IGraphState> extends GraphPartBase<T> implements NodeAction, IGraphNode {

    /**
     * 构造函数，初始化图节点基本信息
     *
     * @param name 节点名称
     * @param graph 所属的代理图对象
     */
    public GraphNodeBase(String name, IAgentGraph graph) {
        super(name, graph);

        // 将当前节点添加到图的节点集合中
        getGraph().getGraphNodes().add(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    /**
     * 应用节点逻辑处理方法
     *
     * @param state 全局状态对象
     * @return 处理结果的键值对映射
     * @throws Exception 处理过程中可能抛出的异常
     */
    public Map<String, Object> apply(OverAllState state) throws Exception {

        // 获取图状态信息
        StateInfo stateInfo = getGraphState(state);

        // 调用抽象方法执行具体的节点逻辑处理
        IUpdatedGraphState<T> updatedGraphState = apply((T) stateInfo.getGraphState(),
                stateInfo.getAgentRuntimeContext(),
                stateInfo.getAgentEvent());

        // 通知图对象节点已应用处理
        getGraph().onGraphPartApplild(this, updatedGraphState, stateInfo.getGraphState(), stateInfo.getAgentRuntimeContext(), stateInfo.getAgentEvent());

        // 执行更新后的图状态并返回结果
        return updatedGraphState.exec();
    }

    /**
     * 抽象方法，由子类实现具体的节点应用逻辑
     *
     * @param graphState 图状态对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentEvent 代理事件对象
     * @return 更新后的图状态对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    public abstract IUpdatedGraphState<T> apply(T graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception;

}

