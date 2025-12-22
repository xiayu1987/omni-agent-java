package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;

/**
 * 代理会话管理接口
 * 定义了代理会话的创建、查询、管理和运行时上下文操作的相关方法
 */
public interface IAgentSessionManage {

    /**
     * 根据会话ID获取代理会话
     * @param sessionId 会话唯一标识符
     * @return 对应的代理会话对象，如果不存在则返回null
     */
    IAgentSession getAgentSessionById(String sessionId);

    /**
     * 根据用户ID获取该用户的所有代理会话
     * @param userId 用户唯一标识符
     * @return 该用户的所有代理会话列表
     */
    List<IAgentSession> getAgentSessionByUserId(String userId);

    /**
     * 根据会话ID和运行时上下文ID获取代理运行时上下文
     * @param sessionId 会话唯一标识符
     * @param runtimeContextId 运行时上下文唯一标识符
     * @return 对应的代理运行时上下文对象
     */
    IAgentRuntimeContext getAgentRuntimeContextById(String sessionId, String runtimeContextId);

    /**
     * 创建新的代理会话
     * @param parentSessionId 父会话ID，用于会话层级关系管理
     * @param userType 用户类型枚举值
     * @param userId 用户唯一标识符
     * @return 新创建的代理会话对象
     */
    IAgentSession createAgentSession(String parentSessionId, EUserType userType, String userId);

    /**
     * 获取指定代理会话的当前会话项
     * @param agentSession 代理会话对象
     * @return 当前会话项对象
     */
    IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession);

    /**
     * 向代理会话中添加会话项
     * @param agentSession 目标代理会话对象
     * @param sessionItem 要添加的会话项对象
     */
    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    /**
     * 向代理会话中添加运行时上下文
     * @param agentSession 目标代理会话对象
     * @param runtimeContext 要添加的运行时上下文对象
     */
    void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

    /**
     * 更新代理会话中的运行时上下文
     * @param agentSession 目标代理会话对象
     * @param runtimeContext 要更新的运行时上下文对象
     */
    void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

}
