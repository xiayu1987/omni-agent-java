package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
/**
 * IAgentSession接口定义了代理会话的相关操作方法。
 * 该接口提供了对代理会话ID、父会话ID、用户类型、用户ID等属性的访问和设置功能，
 * 同时管理代理会话项和运行时上下文的集合。
 */
public interface IAgentSession {

    /**
     * 获取代理会话ID
     *
     * @return 返回当前代理会话的唯一标识符字符串
     */
    String getAgentSessionId();

    /**
     * 设置代理会话ID
     *
     * @param agentSessionId 代理会话的唯一标识符
     */
    void setAgentSessionId(String agentSessionId);

    /**
     * 获取父会话ID
     *
     * @return 返回当前会话的父级会话唯一标识符字符串
     */
    String getParentSessionId();

    /**
     * 设置父会话ID
     *
     * @param parentSessionId 父级会话的唯一标识符
     */
    void setParentSessionId(String parentSessionId);

    /**
     * 获取用户类型
     *
     * @return 返回用户的类型枚举值
     */
    EUserType getUserType();

    /**
     * 设置用户类型
     *
     * @param userType 用户类型的枚举值
     */
    void setUserType(EUserType userType);

    /**
     * 获取用户ID
     *
     * @return 返回用户的唯一标识符字符串
     */
    String getUserId();

    /**
     * 设置用户ID
     *
     * @param userId 用户的唯一标识符
     */
    void setUserId(String userId);

    /**
     * 获取代理会话项列表
     *
     * @return 返回包含所有代理会话项的列表
     */
    List<IAgentSessionItem> getAgentSessionItems();

    /**
     * 设置代理会话项列表
     *
     * @param agentSessionItems 包含代理会话项的列表
     */
    void setAgentSessionItems(List<IAgentSessionItem> agentSessionItems);

    /**
     * 获取代理运行时上下文列表
     *
     * @return 返回包含所有代理运行时上下文的列表
     */
    List<IAgentRuntimeContext> getAgentRuntimeContexts();

    /**
     * 获取当前运行时上下文
     *
     * @return 返回当前活动的代理运行时上下文对象
     */
    IAgentRuntimeContext getCurrentRuntimeContext();

    /**
     * 设置代理运行时上下文列表
     *
     * @param agentRuntimeContexts 包含代理运行时上下文的列表
     */
    void setAgentRuntimeContexts(List<IAgentRuntimeContext> agentRuntimeContexts);
}

