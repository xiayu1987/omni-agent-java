package com.beraising.agent.omni.core.session.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.common.ListUtils;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionManage;
import com.beraising.agent.omni.core.session.ISessionStore;
import com.beraising.agent.omni.core.session.config.AgentSessionProperties;
import com.beraising.agent.omni.core.session.config.AgentSessionProperties.StorageType;

import jakarta.annotation.PostConstruct;

import com.beraising.agent.omni.core.session.IAgentSessionItem;
/**
 * Agent会话管理器实现类。
 * 负责根据配置选择合适的会话存储方式，并提供对Agent会话及其相关数据（如会话项、运行时上下文）的增删改查操作。
 */
@Component
public class AgentSessionManage implements IAgentSessionManage {

    /**
     * 存储类型到会话存储实例的映射表。
     */
    private final Map<String, ISessionStore> sessionStoreMap;

    /**
     * 会话相关的配置属性。
     */
    private final AgentSessionProperties properties;

    /**
     * 实际使用的会话存储实例，在初始化后确定。
     */
    private ISessionStore storage;

    /**
     * 构造方法。
     *
     * @param sessionStoreMap 会话存储实现类的映射表，键为存储类型名称的小写形式
     * @param properties      配置属性对象
     */
    public AgentSessionManage(Map<String, ISessionStore> sessionStoreMap,
            AgentSessionProperties properties) {
        this.sessionStoreMap = sessionStoreMap;
        this.properties = properties;
    }

    /**
     * 初始化方法，在Bean构造完成后执行。
     * 根据配置中的存储类型从sessionStoreMap中获取对应的会话存储实例。
     * 若未找到对应类型的存储实例则抛出异常。
     */
    @PostConstruct
    public void init() {
        StorageType storageType = properties.getStoreType();
        this.storage = sessionStoreMap.get(storageType.name().toLowerCase());

        if (this.storage == null) {
            throw new IllegalStateException(
                    "No ISessionStore found for type: '" + storageType + "'. " +
                            "Available: " + sessionStoreMap.keySet());
        }
    }

    /**
     * 根据会话ID获取Agent会话信息。
     *
     * @param sessionId 会话唯一标识符
     * @return 对应的Agent会话对象，若不存在则可能返回null或由具体存储决定
     */
    @Override
    public IAgentSession getAgentSessionById(String sessionId) {
        return this.storage.selectById(sessionId);
    }

    /**
     * 根据用户ID查询所有关联的Agent会话列表。
     *
     * @param userId 用户唯一标识符
     * @return 匹配的所有Agent会话列表
     */
    @Override
    public List<IAgentSession> getAgentSessionByUserId(String userId) {
        return this.storage.selectByUserId(userId);
    }

    /**
     * 创建一个新的Agent会话。
     * 自动生成唯一的会话ID并保存至存储系统。
     *
     * @param parentSessionId 父级会话ID（可为空）
     * @param userType        用户类型
     * @param userId          用户ID
     * @return 新创建的Agent会话对象
     */
    @Override
    public IAgentSession createAgentSession(String parentSessionId, EUserType userType, String userId) {
        IAgentSession newSession = new AgentSession();
        newSession.setAgentSessionId(UUID.randomUUID().toString());
        newSession.setUserId(userId);
        newSession.setUserType(userType);
        newSession.setParentSessionId(parentSessionId);

        this.storage.saveSession(newSession);

        return newSession;
    }

    /**
     * 获取当前最新的会话项。
     *
     * @param agentSession Agent会话对象
     * @return 最新的会话项，如果无可用项则返回null
     */
    @Override
    public IAgentSessionItem getCurrentSessionItem(IAgentSession agentSession) {
        return ListUtils.lastOf(agentSession.getAgentSessionItems());
    }

    /**
     * 向指定的Agent会话中添加一个新会话项。
     *
     * @param agentSession Agent会话对象
     * @param sessionItem  待添加的会话项
     */
    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession != null && sessionItem != null) {
            this.storage.addSessionItem(agentSession, sessionItem);
        }
    }

    /**
     * 向指定的Agent会话中添加一个新的运行时上下文。
     *
     * @param agentSession   Agent会话对象
     * @param runtimeContext 运行时上下文对象
     */
    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession != null && runtimeContext != null) {
            this.storage.addAgentRuntimeContext(agentSession, runtimeContext);
        }
    }

    /**
     * 更新指定Agent会话中的某个运行时上下文。
     *
     * @param agentSession   Agent会话对象
     * @param runtimeContext 待更新的运行时上下文对象
     */
    @Override
    public void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession != null && runtimeContext != null) {
            this.storage.updateAgentRuntimeContext(agentSession, runtimeContext);
        }
    }

    /**
     * 根据会话ID和运行时上下文ID查找特定的运行时上下文。
     *
     * @param sessionId         会话ID
     * @param runtimeContextId  运行时上下文ID
     * @return 匹配的运行时上下文对象，若未找到则返回null
     */
    @Override
    public IAgentRuntimeContext getAgentRuntimeContextById(String sessionId, String runtimeContextId) {
        // 先通过会话ID获取完整的Agent会话对象
        IAgentSession agentSession = getAgentSessionById(sessionId);
        // 在该会话的运行时上下文中筛选匹配ID的对象
        return agentSession.getAgentRuntimeContexts().stream()
                .filter(agentRuntimeContext -> agentRuntimeContext.getAgentRuntimeContextId().equals(runtimeContextId))
                .findFirst().orElse(null);
    }

}

