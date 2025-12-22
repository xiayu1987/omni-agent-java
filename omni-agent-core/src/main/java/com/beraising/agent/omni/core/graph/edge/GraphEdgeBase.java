package com.beraising.agent.omni.core.graph.edge;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.GraphPartBase;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.state.IGraphState;

/**
 * 图边基类，继承自GraphPartBase并实现EdgeAction和IGraphEdge接口
 * 用于定义图中边的基本行为和属性
 */
public abstract class GraphEdgeBase<T extends IGraphState> extends GraphPartBase<T> implements EdgeAction, IGraphEdge {

    /**
     * 构造函数，初始化图边对象
     * @param name 边的名称
     * @param graph 所属的代理图对象
     */
    public GraphEdgeBase(String name, IAgentGraph graph) {
        super(name, graph);

        // 将当前边添加到图的边集合中
        getGraph().getGraphEdges().add(this);
    }

    /**
     * 应用边操作的入口方法，将总体状态转换为具体的图状态并调用抽象apply方法
     * @param state 总体状态对象
     * @return 操作结果字符串
     * @throws Exception 处理过程中可能抛出的异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public String apply(OverAllState state) throws Exception {

        // 获取图状态信息
        StateInfo stateInfo = getGraphState(state);

        // 调用具体的apply实现方法
        return apply((T) stateInfo.getGraphState(), stateInfo.getAgentRuntimeContext(), stateInfo.getAgentEvent());
    }

    /**
     * 抽象的边应用方法，由子类具体实现
     * @param graphState 图状态对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentEvent 代理事件对象
     * @return 操作结果字符串
     * @throws Exception 处理过程中可能抛出的异常
     */
    public abstract String apply(T graphState, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) throws Exception;
}
