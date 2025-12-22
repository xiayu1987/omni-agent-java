package com.beraising.agent.omni.core.session;

import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;

/**
 * 会话存储接口，定义了对代理会话数据的增删改查操作
 */
public interface ISessionStore {

    /**
     * 根据会话ID查询代理会话
     *
     * @param sessionId 会话唯一标识符
     * @return 代理会话对象，如果未找到则返回null
     */
    IAgentSession selectById(String sessionId);

    /**
     * 根据用户ID查询该用户的所有代理会话
     *
     * @param userId 用户唯一标识符
     * @return 代理会话列表，如果未找到则返回空列表
     */
    List<IAgentSession> selectByUserId(String userId);

    /**
     * 保存代理会话信息
     *
     * @param session 要保存的代理会话对象
     */
    void saveSession(IAgentSession session);

    /**
     * 向指定代理会话中添加会话项
     *
     * @param agentSession 目标代理会话对象
     * @param sessionItem 要添加的会话项对象
     */
    void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem);

    /**
     * 向指定代理会话中添加运行时上下文
     *
     * @param agentSession 目标代理会话对象
     * @param runtimeContext 要添加的运行时上下文对象
     */
    void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

    /**
     * 更新指定代理会话中的运行时上下文
     *
     * @param agentSession 目标代理会话对象
     * @param runtimeContext 要更新的运行时上下文对象
     */
    void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext);

    /**
     * 根据会话ID删除代理会话
     *
     * @param sessionId 要删除的会话唯一标识符
     */
    void delete(String sessionId);
}

