package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@Component("db")
public class DbSessionStore implements ISessionStore {

    private final IAgentSessionMapper sessionMapper;
    private final IAgentSessionItemMapper itemMapper;
    private final IAgentRuntimeContextMapper contextMapper;

    private final Cache<String, IAgentSession> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public DbSessionStore(IAgentSessionMapper sessionMapper,
            IAgentSessionItemMapper itemMapper,
            IAgentRuntimeContextMapper contextMapper) {
        this.sessionMapper = sessionMapper;
        this.itemMapper = itemMapper;
        this.contextMapper = contextMapper;
    }

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

    @Override
    public void delete(String sessionId) {
        itemMapper.deleteBySessionId(sessionId);
        contextMapper.deleteBySessionId(sessionId);
        sessionMapper.deleteById(sessionId);

        cache.invalidate(sessionId);
    }

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

    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }

        AgentRuntimeContextEntity entity = AgentSessionConverter.toEntity(agentSession.getAgentSessionId(),
                runtimeContext);
        contextMapper.insertOrUpdate(entity);

        // 更新缓存
        cache.asMap().computeIfPresent(agentSession.getAgentSessionId(), (key, value) -> {
            value.getAgentRuntimeContexts().add(runtimeContext);
            return value;
        });
    }
}