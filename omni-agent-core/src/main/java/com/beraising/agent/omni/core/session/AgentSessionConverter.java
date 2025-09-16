package com.beraising.agent.omni.core.session;

import java.time.LocalDateTime;
import java.util.List;

import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.impl.AgentRuntimeContext;
import com.beraising.agent.omni.core.session.entity.AgentRuntimeContextEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionEntity;
import com.beraising.agent.omni.core.session.entity.AgentSessionItemEntity;
import com.beraising.agent.omni.core.session.impl.AgentSession;
import com.beraising.agent.omni.core.session.impl.AgentSessionItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.stream.Collectors;

public class AgentSessionConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static IAgentSession toDomain(
            AgentSessionEntity entity,
            List<IAgentSessionItem> items,
            List<IAgentRuntimeContext> contexts) {
        if (entity == null)
            return null;

        AgentSession session = new AgentSession();
        session.setAgentSessionId(entity.getSessionId());
        session.setAgentSessionItems(items != null ? items : List.of());
        session.setAgentRuntimeContexts(contexts != null ? contexts : List.of());

        return session;
    }

    public static AgentSessionEntity toEntity(IAgentSession domain) {
        if (domain == null)
            return null;

        AgentSessionEntity entity = new AgentSessionEntity();
        entity.setSessionId(domain.getAgentSessionId());
        entity.setUserId(domain.getUserId());
        // entity.setUpdateTime(LocalDateTime.now());
        // entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    public static IAgentSessionItem toDomain(AgentSessionItemEntity entity) {
        if (entity == null)
            return null;
        try {
            return objectMapper.readValue(entity.getItemData(), AgentSessionItem.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session item json", e);
        }
    }

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

    public static IAgentRuntimeContext toDomain(AgentRuntimeContextEntity entity) {
        if (entity == null)
            return null;
        try {
            return objectMapper.readValue(entity.getContextData(), AgentRuntimeContext.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse runtime context json", e);
        }
    }

    public static AgentRuntimeContextEntity toEntity(String sessionId, IAgentRuntimeContext domain) {
        if (domain == null)
            return null;

        AgentRuntimeContextEntity entity = new AgentRuntimeContextEntity();
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

    public static IAgentSession toAggregate(
            AgentSessionEntity sessionEntity,
            List<AgentSessionItemEntity> itemEntities,
            List<AgentRuntimeContextEntity> contextEntities) {
        if (sessionEntity == null)
            return null;

        List<IAgentSessionItem> items = itemEntities != null
                ? itemEntities.stream().map(AgentSessionConverter::toDomain).collect(Collectors.toList())
                : List.of();

        List<IAgentRuntimeContext> contexts = contextEntities != null
                ? contextEntities.stream().map(AgentSessionConverter::toDomain).collect(Collectors.toList())
                : List.of();

        return toDomain(sessionEntity, items, contexts);
    }

    public static AggregateEntities toEntities(IAgentSession domain) {
        if (domain == null)
            return null;

        AgentSessionEntity sessionEntity = toEntity(domain);

        List<AgentSessionItemEntity> itemEntities = domain.getAgentSessionItems() != null
                ? domain.getAgentSessionItems().stream()
                        .map(item -> toEntity(domain.getAgentSessionId(), item))
                        .collect(Collectors.toList())
                : List.of();

        List<AgentRuntimeContextEntity> contextEntities = domain.getAgentRuntimeContexts() != null
                ? domain.getAgentRuntimeContexts().stream()
                        .map(ctx -> toEntity(domain.getAgentSessionId(), ctx))
                        .collect(Collectors.toList())
                : List.of();

        return new AggregateEntities(sessionEntity, itemEntities, contextEntities);
    }

    @Data
    @AllArgsConstructor
    public static class AggregateEntities {
        private AgentSessionEntity sessionEntity;
        private List<AgentSessionItemEntity> itemEntities;
        private List<AgentRuntimeContextEntity> contextEntities;
    }
}
