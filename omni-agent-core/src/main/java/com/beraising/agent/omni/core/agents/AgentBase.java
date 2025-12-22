package com.beraising.agent.omni.core.agents;

import java.util.Map;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IAgentResponse;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.graph.IAgentGraphListener;
import com.beraising.agent.omni.core.graph.node.IGraphNode;
import com.beraising.agent.omni.core.graph.state.IGraphState;

/**
 * AgentBase 是一个抽象类，实现了 IAgent 接口。
 * 它提供了代理（Agent）的基本实现框架，包括初始化、事件调用以及图执行逻辑等核心功能。
 */
public abstract class AgentBase implements IAgent {

    /**
     * 用于监听代理相关事件的回调接口实例。
     */
    private IEventListener eventListener;

    /**
     * 初始化代理对象，并设置事件监听器。
     *
     * @param eventListener 事件监听器，用于接收代理运行过程中的各种事件通知。
     * @throws Exception 如果初始化过程中发生错误则抛出异常。
     */
    @Override
    public void init(IEventListener eventListener) throws Exception {
        this.eventListener = eventListener;
        getAgentGraph().init(this, eventListener, new AgentGraphListener(new AgentGraphLifecycleListener()));
    }

    /**
     * 调用代理处理指定的事件。
     *
     * @param agentEvent 需要被代理处理的事件对象。
     * @return 处理后的事件结果对象。
     * @throws Exception 如果在处理过程中出现异常则抛出。
     */
    @Override
    public IAgentEvent invoke(IAgentEvent agentEvent) throws Exception {

        IAgentRuntimeContext runtimeContext = null;

        IAgentGraph agentGraph = getAgentGraph();

        // 在调用前触发 beforeAgentInvoke 回调方法
        runtimeContext = this.eventListener.beforeAgentInvoke(this, agentEvent,
                getAgentGraph());

        try {
            // 执行代理图逻辑
            return agentGraph.invoke(runtimeContext);

        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常时触发 onError 回调方法
            this.eventListener.onError(this, agentEvent, runtimeContext, e);
        }

        return agentEvent;
    }

    // 注释掉的方法 asToolCallback 暂未启用

    /**
     * AgentGraphListener 实现了 IAgentGraphListener 接口，
     * 提供了代理图生命周期中关键节点的监听能力。
     */
    public class AgentGraphListener implements IAgentGraphListener {

        /**
         * 图状态生命周期监听器。
         */
        private GraphLifecycleListener stateGraphLifecycleListener;

        /**
         * 构造函数，初始化图生命周期监听器。
         *
         * @param stateGraphLifecycleListener 图状态生命周期监听器实例。
         */
        public AgentGraphListener(GraphLifecycleListener stateGraphLifecycleListener) {
            super();
            this.stateGraphLifecycleListener = stateGraphLifecycleListener;
        }

        /**
         * 获取当前的状态图生命周期监听器。
         *
         * @return 当前注册的 GraphLifecycleListener 实例。
         */
        @Override
        public GraphLifecycleListener getStateGraphLifecycleListener() {
            return this.stateGraphLifecycleListener;
        }

        /**
         * 设置新的状态图生命周期监听器。
         *
         * @param stateGraphLifecycleListener 新的 GraphLifecycleListener 实例。
         */
        @Override
        public void setStateGraphLifecycleListener(GraphLifecycleListener stateGraphLifecycleListener) {
            this.stateGraphLifecycleListener = stateGraphLifecycleListener;
        }

        /**
         * 当代理图执行中断时触发该方法。
         *
         * @param agent             发生中断的代理对象。
         * @param agentEvent        引发中断的事件对象。
         * @param agentRuntimeContext 运行上下文信息。
         * @param graphNode         中断发生的图节点。
         * @param agentResponse     中断响应数据。
         * @throws Exception 如果处理中断过程中出现问题则抛出异常。
         */
        @Override
        public void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                IGraphNode graphNode, IAgentResponse agentResponse) throws Exception {
            AgentBase.this.eventListener.onInterrupt(AgentBase.this, agentEvent, agentRuntimeContext,
                    AgentBase.this.getAgentGraph().createOutput(agentRuntimeContext,
                            agentEvent, graphNode));
        }

    }

    /**
     * AgentGraphLifecycleListener 实现了 GraphLifecycleListener 接口，
     * 监听代理图生命周期的关键阶段，例如完成状态。
     */
    public class AgentGraphLifecycleListener implements GraphLifecycleListener {

        /**
         * 当某个图节点完成执行后调用此方法。
         *
         * @param nodeId 节点 ID。
         * @param state  当前节点的状态信息。
         */
        @Override
        public void onComplete(String nodeId, Map<String, Object> state) {
            if (nodeId.equals(StateGraph.END)) {
                IAgentEvent agentEvent = null;
                IAgentRuntimeContext runtimeContext = null;

                try {
                    // 根据会话 ID 和上下文 ID 获取对应的运行时上下文
                    IAgentRuntimeContext agentRuntimeContext = AgentBase.this.getAgentStaticContext()
                            .getAgentSessionManage()
                            .getAgentRuntimeContextById(state.get(IGraphState.getAgentSessionIDKey()).toString(),
                                    state.get(IGraphState.getAgentRuntimeContextIDKey()).toString());

                    agentEvent = agentRuntimeContext.getCurrentEvent();

                    // 触发 onComplete 回调方法
                    AgentBase.this.eventListener.onComplete(AgentBase.this, agentEvent, agentRuntimeContext,
                            AgentBase.this.getAgentGraph().createOutput(agentRuntimeContext,
                                    agentEvent, null));

                } catch (Exception e) {
                    e.printStackTrace();
                    // 出错时触发 onError 回调方法
                    AgentBase.this.eventListener.onError(AgentBase.this, agentEvent, runtimeContext, e);
                }
            }
        }
    }
}

