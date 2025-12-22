package com.beraising.agent.omni.core.session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.impl.AgentRuntimeContext;
import com.beraising.agent.omni.core.event.EUserType;
import com.beraising.agent.omni.core.session.entity.AgentRuntimeContextEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionItemEntity;
import com.beraising.agent.omni.core.session.impl.AgentSession;
import com.beraising.agent.omni.core.session.impl.AgentSessionItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.stream.Collectors;
/**
 * 转换器类，用于在领域模型（Domain）与持久化实体（Entity）之间进行转换。
 */
public class AgentSessionConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 {@link AgentSessionEntity} 实体对象转换为领域模型 {@link IAgentSession}。
     *
     * @param entity   数据库中的会话实体对象
     * @param items    该会话关联的会话项列表
     * @param contexts 该会话关联的运行时上下文列表
     * @return 对应的领域模型对象，如果输入为 null 则返回 null
     */
    public static IAgentSession toDomain(
            AgentSessionEntity entity,
            List<IAgentSessionItem> items,
            List<IAgentRuntimeContext> contexts) {
        if (entity == null)
            return null;

        AgentSession session = new AgentSession();
        session.setAgentSessionId(entity.getSessionId());
        session.setParentSessionId(entity.getParentSessionId());
        session.setUserId(entity.getUserId());
        session.setUserType(EUserType.fromCode(entity.getUserType()));
        session.setAgentSessionItems(items != null ? items : List.of());
        session.setAgentRuntimeContexts(contexts != null ? contexts : List.of());

        return session;
    }

    /**
     * 将领域模型 {@link IAgentSession} 转换为数据库实体 {@link AgentSessionEntity}。
     *
     * @param domain 领域模型对象
     * @return 对应的数据库实体对象，如果输入为 null 则返回 null
     */
    public static AgentSessionEntity toEntity(IAgentSession domain) {
        if (domain == null)
            return null;

        AgentSessionEntity entity = new AgentSessionEntity();
        entity.setSessionId(domain.getAgentSessionId());
        entity.setParentSessionId(domain.getParentSessionId());
        entity.setUserId(domain.getUserId());
        entity.setUserType(
                Optional.ofNullable(domain.getUserType())
                        .orElse(EUserType.USER) // 默认 USER
                        .getCode());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    /**
     * 将 {@link AgentSessionItemEntity} 实体对象反序列化为领域模型 {@link IAgentSessionItem}。
     *
     * @param entity 数据库中存储的会话项实体对象
     * @return 反序列化后的领域模型对象，如果输入为 null 则返回 null
     * @throws RuntimeException 当 JSON 解析失败时抛出异常
     */
    public static IAgentSessionItem toDomain(AgentSessionItemEntity entity) {
        if (entity == null)
            return null;
        try {
            return objectMapper.readValue(entity.getItemData(), AgentSessionItem.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session item json", e);
        }
    }

    /**
     * 将领域模型 {@link IAgentSessionItem} 序列化并封装成数据库实体 {@link AgentSessionItemEntity}。
     *
     * @param sessionId 会话 ID
     * @param domain    领域模型对象
     * @return 包含序列化数据的数据库实体对象，如果输入为 null 则返回 null
     * @throws RuntimeException 当序列化失败时抛出异常
     */
    public static AgentSessionItemEntity toEntity(String sessionId, IAgentSessionItem domain) {
        if (domain == null)
            return null;

        AgentSessionItemEntity entity = new AgentSessionItemEntity();
        entity.setSessionId(sessionId);
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreateTime(LocalDateTime.now());

        try {
            entity.setItemData(objectMapper.writeValueAsString(domain));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize session item", e);
        }

        return entity;
    }

    /**
     * 将 {@link AgentRuntimeContextEntity} 实体对象反序列化为领域模型 {@link IAgentRuntimeContext}。
     *
     * @param entity 数据库中存储的运行时上下文实体对象
     * @return 反序列化后的领域模型对象，如果输入为 null 则返回 null
     * @throws RuntimeException 当 JSON 解析失败时抛出异常
     */
    public static IAgentRuntimeContext toDomain(AgentRuntimeContextEntity entity) {
        if (entity == null)
            return null;
        try {
            return objectMapper.readValue(entity.getContextData(), AgentRuntimeContext.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse runtime context json", e);
        }
    }

    /**
     * 将领域模型 {@link IAgentRuntimeContext} 序列化并封装成数据库实体 {@link AgentRuntimeContextEntity}。
     *
     * @param sessionId 会话 ID
     * @param domain    领域模型对象
     * @return 包含序列化数据的数据库实体对象，如果输入为 null 则返回 null
     * @throws RuntimeException 当序列化失败时抛出异常
     */
    public static AgentRuntimeContextEntity toEntity(String sessionId, IAgentRuntimeContext domain) {
        if (domain == null)
            return null;

        AgentRuntimeContextEntity entity = new AgentRuntimeContextEntity();
        entity.setId(domain.getAgentRuntimeContextId());
        entity.setSessionId(sessionId);
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreateTime(LocalDateTime.now());

        try {
            entity.setContextData(objectMapper.writeValueAsString(domain));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize runtime context", e);
        }

        return entity;
    }

    /**
     * 将完整的聚合根实体信息（包括主实体、子项和上下文）组装为一个完整的领域模型 {@link IAgentSession}。
     *
     * @param sessionEntity   主要的会话实体
     * @param itemEntities    关联的会话项实体列表
     * @param contextEntities 关联的运行时上下文实体列表
     * @return 完整的领域模型对象，如果主实体为空则返回 null
     */
    public static IAgentSession toAggregate(
            AgentSessionEntity sessionEntity,
            List<AgentSessionItemEntity> itemEntities,
            List<AgentRuntimeContextEntity> contextEntities) {
        if (sessionEntity == null)
            return null;

        // 转换所有会话项实体为领域模型
        List<IAgentSessionItem> items = itemEntities != null
                ? itemEntities.stream().map(AgentSessionConverter::toDomain).collect(Collectors.toList())
                : List.of();

        // 转换所有运行时上下文实体为领域模型
        List<IAgentRuntimeContext> contexts = contextEntities != null
                ? contextEntities.stream().map(AgentSessionConverter::toDomain).collect(Collectors.toList())
                : List.of();

        return toDomain(sessionEntity, items, contexts);
    }

    /**
     * 将领域模型 {@link IAgentSession} 拆解为一组对应的数据库实体集合。
     *
     * @param domain 领域模型对象
     * @return 包含主实体及其相关子实体的对象容器，如果输入为 null 则返回 null
     */
    public static AggregateEntities toEntities(IAgentSession domain) {
        if (domain == null)
            return null;

        AgentSessionEntity sessionEntity = toEntity(domain);

        // 构建会话项实体列表
        List<AgentSessionItemEntity> itemEntities = domain.getAgentSessionItems() != null
                ? domain.getAgentSessionItems().stream()
                        .map(item -> toEntity(domain.getAgentSessionId(), item))
                        .collect(Collectors.toList())
                : List.of();

        // 构建运行时上下文实体列表
        List<AgentRuntimeContextEntity> contextEntities = domain.getAgentRuntimeContexts() != null
                ? domain.getAgentRuntimeContexts().stream()
                        .map(ctx -> toEntity(domain.getAgentSessionId(), ctx))
                        .collect(Collectors.toList())
                : List.of();

        return new AggregateEntities(sessionEntity, itemEntities, contextEntities);
    }

    /**
     * 表示一个聚合根的所有实体集合，包含主实体、会话项实体和运行时上下文实体。
     */
    @Data
    @AllArgsConstructor
    public static class AggregateEntities {
        private AgentSessionEntity sessionEntity;
        private List<AgentSessionItemEntity> itemEntities;
        private List<AgentRuntimeContextEntity> contextEntities;
    }
}

