package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.session.AgentSessionConverter;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.ISessionStore;
import com.beraising.agent.omni.core.session.entity.AgentRuntimeContextEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionItemEntity;
import com.beraising.agent.omni.core.session.mapper.IAgentRuntimeContextMapper;
import com.beraising.agent.omni.core.session.mapper.IAgentSessionItemMapper;
import com.beraising.agent.omni.core.session.mapper.IAgentSessionMapper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
/**
 * 基于数据库实现的会话存储服务。
 * <p>
 * 该类实现了 {@link ISessionStore} 接口，用于将会话数据持久化到数据库中，并通过本地缓存提升访问性能。
 * 使用 Caffeine 实现一级缓存，减少对数据库的频繁访问。
 * </p>
 *
 * @author [作者名]
 * @since [版本号]
 */
@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@Component("db")
public class DbSessionStore implements ISessionStore {

    private final IAgentSessionMapper sessionMapper;
    private final IAgentSessionItemMapper itemMapper;
    private final IAgentRuntimeContextMapper contextMapper;

    /**
     * 本地缓存：缓存会话对象，过期时间为30分钟，最大容量为10000个。
     */
    private final Cache<String, IAgentSession> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    /**
     * 构造方法，注入所需的 Mapper 组件。
     *
     * @param sessionMapper   会话主表操作接口
     * @param itemMapper      会话项表操作接口
     * @param contextMapper   运行时上下文表操作接口
     */
    public DbSessionStore(IAgentSessionMapper sessionMapper,
                          IAgentSessionItemMapper itemMapper,
                          IAgentRuntimeContextMapper contextMapper) {
        this.sessionMapper = sessionMapper;
        this.itemMapper = itemMapper;
        this.contextMapper = contextMapper;
    }

    /**
     * 根据会话ID查询会话信息。
     * <p>
     * 查询流程如下：
     * 1. 先从本地缓存中查找；
     * 2. 若缓存未命中，则从数据库加载主记录、关联项及运行时上下文；
     * 3. 将结果转换为聚合对象后写入缓存并返回。
     * </p>
     *
     * @param sessionId 会话ID
     * @return 对应的会话对象，若不存在则返回 null
     */
    @Override
    public IAgentSession selectById(String sessionId) {
        // 先查缓存
        IAgentSession session = cache.getIfPresent(sessionId);
        if (session != null) {
            return session;
        }

        // 缓存未命中，查数据库
        AgentSessionEntity sessionEntity = sessionMapper.findById(sessionId);
        if (sessionEntity == null) {
            return null;
        }

        List<AgentSessionItemEntity> items = itemMapper.findBySessionId(sessionId);
        List<AgentRuntimeContextEntity> contexts = contextMapper.findBySessionId(sessionId);

        session = AgentSessionConverter.toAggregate(sessionEntity, items, contexts);

        // 写入缓存
        cache.put(sessionId, session);
        return session;
    }

    /**
     * 根据用户ID查询其所有会话信息。
     * <p>
     * 查询流程如下：
     * 1. 直接从数据库获取该用户的全部会话主记录；
     * 2. 遍历每条主记录，尝试从缓存中获取完整会话对象；
     * 3. 若缓存未命中，则从数据库加载相关联的数据并构建完整对象；
     * 4. 最终将完整的会话列表返回。
     * </p>
     *
     * @param userId 用户ID
     * @return 该用户的所有会话列表，可能为空但不会为 null
     */
    @Override
    public List<IAgentSession> selectByUserId(String userId) {
        // 先查数据库（这里一般不会走单条缓存，因为是批量查询）
        List<AgentSessionEntity> sessionEntities = sessionMapper.findByUserId(userId);
        if (sessionEntities == null || sessionEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<IAgentSession> result = new ArrayList<>();

        for (AgentSessionEntity sessionEntity : sessionEntities) {
            String sessionId = sessionEntity.getSessionId();

            // 先查缓存
            IAgentSession session = cache.getIfPresent(sessionId);
            if (session == null) {
                // 缓存未命中，查子表
                List<AgentSessionItemEntity> items = itemMapper.findBySessionId(sessionId);
                List<AgentRuntimeContextEntity> contexts = contextMapper.findBySessionId(sessionId);

                session = AgentSessionConverter.toAggregate(sessionEntity, items, contexts);

                // 写入缓存
                cache.put(sessionId, session);
            }

            result.add(session);
        }

        return result;
    }

    /**
     * 保存或更新一个会话及其相关的子项和上下文信息。
     * <p>
     * 操作步骤包括：
     * 1. 转换聚合对象为多个实体对象；
     * 2. 分别插入或更新主表、子项表和上下文表；
     * 3. 同步更新本地缓存中的对应会话。
     * </p>
     *
     * @param session 待保存的会话对象
     */
    @Override
    public void saveSession(IAgentSession session) {
        if (session == null) {
            return;
        }

        AgentSessionConverter.AggregateEntities entities = AgentSessionConverter.toEntities(session);

        sessionMapper.insertOrUpdate(entities.getSessionEntity());
        if (entities.getItemEntities() != null) {
            for (AgentSessionItemEntity item : entities.getItemEntities()) {
                itemMapper.insertOrUpdate(item);
            }
        }
        if (entities.getContextEntities() != null) {
            for (AgentRuntimeContextEntity ctx : entities.getContextEntities()) {
                contextMapper.insertOrUpdate(ctx);
            }
        }

        // 同步缓存
        cache.put(session.getAgentSessionId(), session);
    }

    /**
     * 删除指定会话及其所有相关数据。
     * <p>
     * 包括删除会话项、运行时上下文以及主记录本身，并清除对应的缓存条目。
     * </p>
     *
     * @param sessionId 待删除的会话ID
     */
    @Override
    public void delete(String sessionId) {
        itemMapper.deleteBySessionId(sessionId);
        contextMapper.deleteBySessionId(sessionId);
        sessionMapper.deleteById(sessionId);

        cache.invalidate(sessionId);
    }

    /**
     * 向指定会话中添加一个新的会话项。
     * <p>
     * 插入新项至数据库，并同步更新内存缓存中的会话结构。
     * </p>
     *
     * @param agentSession 当前会话对象
     * @param sessionItem  新增的会话项
     */
    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession == null || sessionItem == null) {
            return;
        }

        AgentSessionItemEntity entity = AgentSessionConverter.toEntity(agentSession.getAgentSessionId(), sessionItem);
        itemMapper.insertOrUpdate(entity);

        // 更新缓存
        cache.asMap().computeIfPresent(agentSession.getAgentSessionId(), (key, value) -> {
            value.getAgentSessionItems().add(sessionItem);
            return value;
        });
    }

    /**
     * 向指定会话中添加一个新的运行时上下文。
     * <p>
     * 插入新的上下文至数据库，并同步更新内存缓存中的会话结构。
     * </p>
     *
     * @param agentSession    当前会话对象
     * @param runtimeContext  新增的运行时上下文
     */
    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }

        AgentRuntimeContextEntity entity = AgentSessionConverter.toEntity(agentSession.getAgentSessionId(),
                runtimeContext);
        contextMapper.insert(entity);

        // 更新缓存
        cache.asMap().computeIfPresent(agentSession.getAgentSessionId(), (key, value) -> {
            value.getAgentRuntimeContexts().add(runtimeContext);
            return value;
        });
    }

    /**
     * 更新指定会话中的某个运行时上下文。
     * <p>
     * 步骤包括：
     * 1. 更新数据库中的对应记录；
     * 2. 在缓存中定位并替换旧的上下文对象以保证一致性。
     * </p>
     *
     * @param agentSession    当前会话对象
     * @param runtimeContext  待更新的运行时上下文
     */
    @Override
    public void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }

        // 先更新数据库
        AgentRuntimeContextEntity entity = AgentSessionConverter.toEntity(
                agentSession.getAgentSessionId(),
                runtimeContext);
        contextMapper.updateById(entity);

        // 再更新缓存，保持和 DB 一致
        cache.asMap().computeIfPresent(agentSession.getAgentSessionId(), (key, value) -> {
            List<IAgentRuntimeContext> contexts = value.getAgentRuntimeContexts();
            if (contexts != null) {
                // 根据唯一 ID 定位并替换
                for (int i = 0; i < contexts.size(); i++) {
                    if (Objects.equals(
                            contexts.get(i).getAgentRuntimeContextId(),
                            runtimeContext.getAgentRuntimeContextId())) {
                        contexts.set(i, runtimeContext);
                        return value;
                    }
                }
            }
            return value;
        });
    }
}
