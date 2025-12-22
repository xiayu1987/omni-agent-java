package com.beraising.agent.omni.core.graph;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
/**
 * 图形部件接口，定义了图形部件的基本操作和属性访问方法
 */
public interface IGraphPart {

    /**
     * 获取图形部件的名称
     *
     * @return 返回图形部件的名称字符串
     */
    String getName();

    /**
     * 获取关联的代理图形对象
     *
     * @return 返回与当前部件关联的IAgentGraph实例
     */
    IAgentGraph getGraph();

    /**
     * 根据整体状态获取图形状态信息
     *
     * @param state 整体状态对象，用于确定图形的当前状态
     * @return 返回包含图形状态、运行时上下文和事件信息的状态信息对象
     * @throws Exception 当状态获取过程中发生错误时抛出异常
     */
    StateInfo getGraphState(OverAllState state) throws Exception;

    /**
     * 状态信息类，封装了图形状态相关的各种信息
     */
    class StateInfo {
        private IGraphState graphState;
        private IAgentRuntimeContext agentRuntimeContext;
        private IAgentEvent agentEvent;

        /**
         * 构造函数，初始化状态信息对象
         *
         * @param graphState 图形状态对象
         * @param agentRuntimeContext 代理运行时上下文
         * @param agentEvent 代理事件对象
         */
        public StateInfo(IGraphState graphState, IAgentRuntimeContext agentRuntimeContext, IAgentEvent agentEvent) {
            this.graphState = graphState;
            this.agentRuntimeContext = agentRuntimeContext;
            this.agentEvent = agentEvent;
        }

        /**
         * 获取图形状态
         *
         * @return 返回IGraphState类型的图形状态对象
         */
        public IGraphState getGraphState() {
            return graphState;
        }

        /**
         * 获取代理运行时上下文
         *
         * @return 返回IAgentRuntimeContext类型的运行时上下文对象
         */
        public IAgentRuntimeContext getAgentRuntimeContext() {
            return agentRuntimeContext;
        }

        /**
         * 获取代理事件
         *
         * @return 返回IAgentEvent类型的代理事件对象
         */
        public IAgentEvent getAgentEvent() {
            return agentEvent;
        }
    }
}

