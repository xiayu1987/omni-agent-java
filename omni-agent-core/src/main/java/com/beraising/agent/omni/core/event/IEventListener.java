package com.beraising.agent.omni.core.event;

import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.graph.IAgentGraph;
import com.beraising.agent.omni.core.session.IAgentSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
public interface IEventListener {

    /**
     * 当事件开始时调用此方法
     *
     * @param parentSession 父会话对象
     * @param agentEvent 代理事件对象
     * @return 返回一个新的代理会话对象
     * @throws Exception 抛出异常
     */
    IAgentSession onStart(IAgentSession parentSession, IAgentEvent agentEvent) throws Exception;

    /**
     * 当发生协作事件时调用此方法
     *
     * @param agent 代理对象
     * @param userEvent 用户事件对象
     * @param collabEvent 协作事件对象
     */
    void onCollab(IAgent agent, IAgentEvent userEvent, IAgentEvent collabEvent);

    /**
     * 在代理调用之前执行的方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentGraph 代理图对象
     * @return 返回代理运行时上下文对象
     * @throws Exception 抛出异常
     */
    IAgentRuntimeContext beforeAgentInvoke(IAgent agent, IAgentEvent agentEvent, IAgentGraph agentGraph)
            throws Exception;

    /**
     * 当代理调用流数据时执行的方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param content 流内容对象
     */
    void onInvokeStream(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                        StreamContent content);

    /**
     * 当图开始执行时调用此方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     */
    void onStartGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext);

    /**
     * 当图执行结束时调用此方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     */
    void onEndGraph(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext);

    /**
     * 当代理执行完成时调用此方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentResponse 代理响应对象
     */
    void onComplete(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                    IAgentResponse agentResponse);

    /**
     * 当发生错误时调用此方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param throwable 异常对象
     */
    void onError(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                 Throwable throwable);

    /**
     * 当代理执行被中断时调用此方法
     *
     * @param agent 代理对象
     * @param agentEvent 代理事件对象
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentResponse 代理响应对象
     */
    void onInterrupt(IAgent agent, IAgentEvent agentEvent, IAgentRuntimeContext agentRuntimeContext,
                     IAgentResponse agentResponse);

    /**
     * 流内容数据类，用于封装流式传输的内容信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class StreamContent {

        /**
         * 标识是否为错误内容
         */
        private boolean isError;

        /**
         * 标识流是否已完成
         */
        private boolean isComplete;

        /**
         * 流内容文本
         */
        private String content;

    }

}

