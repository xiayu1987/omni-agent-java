package com.beraising.agent.omni.core.context;

import java.util.List;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.beraising.agent.omni.core.agents.IAgent;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.IGraphState;
/**
 * IAgentRuntimeContext接口继承自IAgentContext，定义了代理运行时上下文的相关操作方法。
 * 该接口提供了对图状态、代理事件、代理实例等运行时信息的管理功能。
 */
public interface IAgentRuntimeContext extends IAgentContext {

    /**
     * 获取图状态对象
     *
     * @return 返回当前的图状态对象IGraphState
     */
    IGraphState getGraphState();

    /**
     * 设置图状态对象
     *
     * @param graphState 图状态对象
     */
    void setGraphState(IGraphState graphState);

    /**
     * 获取所有代理事件列表
     *
     * @return 返回代理事件列表
     */
    List<IAgentEvent> getAgentEvents();

    /**
     * 根据用户类型获取代理事件列表
     *
     * @param userType 用户类型枚举值
     * @return 返回指定用户类型的代理事件列表
     */
    List<IAgentEvent> getAgentEventsByUserType(EUserType userType);

    /**
     * 获取当前代理事件
     *
     * @return 返回当前代理事件对象
     */
    IAgentEvent getCurrentEvent();

    /**
     * 设置代理事件列表
     *
     * @param agentEvents 代理事件列表
     */
    void setAgentEvents(List<IAgentEvent> agentEvents);

    /**
     * 获取代理实例
     *
     * @return 返回代理实例对象
     */
    IAgent getAgent();

    /**
     * 设置代理实例
     *
     * @param agent 代理实例对象
     */
    void setAgent(IAgent agent);

    /**
     * 获取编译后的图对象
     *
     * @return 返回编译后的图对象
     */
    CompiledGraph getCompiledGraph();

    /**
     * 设置编译后的图对象
     *
     * @param compiledGraph 编译后的图对象
     */
    void setCompiledGraph(CompiledGraph compiledGraph);

    /**
     * 获取代理会话ID
     *
     * @return 返回代理会话ID字符串
     */
    String getAgentSessionId();

    /**
     * 设置代理会话ID
     *
     * @param agentSessionId 代理会话ID字符串
     */
    void setAgentSessionId(String agentSessionId);

    /**
     * 获取代理运行时上下文ID
     *
     * @return 返回代理运行时上下文ID字符串
     */
    String getAgentRuntimeContextId();

    /**
     * 设置代理运行时上下文ID
     *
     * @param agentRuntimeContextId 代理运行时上下文ID字符串
     */
    void setAgentRuntimeContextId(String agentRuntimeContextId);

    /**
     * 获取代理名称
     *
     * @return 返回代理名称字符串
     */
    String getAgentName();

    /**
     * 设置代理名称
     *
     * @param agentName 代理名称字符串
     */
    void setAgentName(String agentName);

    /**
     * 获取图运行状态
     *
     * @return 返回图运行状态值
     */
    int getGraphRunStatus();

    /**
     * 设置图运行状态
     *
     * @param graphRunStatus 图运行状态值
     */
    void setGraphRunStatus(int graphRunStatus);

    /**
     * 判断是否结束
     *
     * @return 返回布尔值表示是否结束
     */
    boolean isEnd();

    /**
     * 设置是否结束状态
     *
     * @param isEnd 布尔值表示是否结束
     */
    void setIsEnd(boolean isEnd);

}

