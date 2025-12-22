package com.beraising.agent.omni.core.agents;

import com.beraising.agent.omni.core.context.IAgentStaticContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.event.IEventListener;
import com.beraising.agent.omni.core.graph.IAgentGraph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
public interface IAgent {

    /**
     * 获取代理名称
     *
     * @return 代理名称字符串
     */
    String getName();

    /**
     * 获取代理描述信息
     *
     * @return 代理描述字符串
     */
    String getDescription();

    /**
     * 获取代理静态上下文
     *
     * @return 代理静态上下文对象
     */
    IAgentStaticContext getAgentStaticContext();

    /**
     * 获取代理图结构
     *
     * @return 代理图对象
     */
    IAgentGraph getAgentGraph();

    /**
     * 初始化代理
     *
     * @param eventListener 事件监听器
     * @throws Exception 初始化过程中可能抛出的异常
     */
    void init(IEventListener eventListener) throws Exception;

    /**
     * 调用代理执行事件
     *
     * @param agentEvent 代理事件对象
     * @return 执行结果的代理事件对象
     * @throws Exception 执行过程中可能抛出的异常
     */
    IAgentEvent invoke(IAgentEvent agentEvent) throws Exception;

    // FunctionToolCallback<AsToolRequest, AsToolResponse>
    // asToolCallback(IAgentEvent agentEvent);

    /**
     * 作为工具请求的参数类
     * 用于封装工具调用时的请求参数
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AsToolRequest {
        /**
         * 会话ID，用于标识一次会话
         */
        private String sessionID;
    }

    /**
     * 作为工具响应的结果类
     * 用于封装工具调用后的响应结果
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class AsToolResponse {
        /**
         * 执行是否成功
         */
        private boolean isSuccess;
        /**
         * 响应消息内容
         */
        private String message;
    }
}

