package com.beraising.agent.omni.core.session.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.ISessionStore;
/**
 * 基于内存的会话存储实现类
 * 使用ConcurrentHashMap作为底层存储结构，提供线程安全的会话管理功能
 */
@Component("memory")
public class MemorySessionStore implements ISessionStore {
    private final Map<String, IAgentSession> store = new ConcurrentHashMap<>();

    /**
     * 根据会话ID查询会话信息
     *
     * @param sessionId 会话唯一标识符
     * @return 对应的会话对象，如果不存在则返回null
     */
    @Override
    public IAgentSession selectById(String sessionId) {
        return store.get(sessionId);
    }

    /**
     * 根据用户ID查询所有相关的会话信息
     *
     * @param userId 用户唯一标识符
     * @return 该用户的所有会话列表
     */
    @Override
    public List<IAgentSession> selectByUserId(String userId) {
        // 过滤出指定用户的所有会话
        return store.values().stream().filter(session -> session.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * 保存会话信息
     *
     * @param session 要保存的会话对象
     */
    @Override
    public void saveSession(IAgentSession session) {
        store.put(session.getAgentSessionId(), session);
    }

    /**
     * 删除指定会话
     *
     * @param sessionId 要删除的会话ID
     */
    @Override
    public void delete(String sessionId) {
        store.remove(sessionId);
    }

    /**
     * 向会话中添加会话项
     *
     * @param agentSession 目标会话对象
     * @param sessionItem 要添加的会话项
     */
    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession == null || sessionItem == null) {
            return;
        }
        agentSession.getAgentSessionItems().add(sessionItem);
    }

    /**
     * 向会话中添加运行时上下文
     *
     * @param agentSession 目标会话对象
     * @param runtimeContext 要添加的运行时上下文
     */
    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }
        agentSession.getAgentRuntimeContexts().add(runtimeContext);
    }

    /**
     * 更新会话中的运行时上下文
     *
     * @param agentSession 目标会话对象
     * @param runtimeContext 要更新的运行时上下文
     */
    @Override
    public void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {

    }

}

