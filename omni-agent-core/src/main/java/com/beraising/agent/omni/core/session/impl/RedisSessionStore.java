package com.beraising.agent.omni.core.session.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.context.impl.AgentRuntimeContext;
import com.beraising.agent.omni.core.session.IAgentSession;
import com.beraising.agent.omni.core.session.IAgentSessionItem;
import com.beraising.agent.omni.core.session.ISessionStore;

@Component("redis")
public class RedisSessionStore implements ISessionStore {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "agent:session:";

    public RedisSessionStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public IAgentSession selectById(String sessionId) {
        String json = redisTemplate.opsForValue().get(PREFIX + sessionId);
        if (json == null) {
            return null;
        }

        AgentSession session = JSON.parseObject(json, AgentSession.class);

        // 读取 Items
        List<String> itemJsons = redisTemplate.opsForList().range(PREFIX + sessionId + ":items", 0, -1);
        if (itemJsons != null) {
            List<IAgentSessionItem> items = itemJsons.stream()
                    .map(s -> JSON.parseObject(s, AgentSessionItem.class))
                    .collect(Collectors.toList());
            session.setAgentSessionItems(items);
        }

        // 读取 Contexts
        List<String> contextJsons = redisTemplate.opsForList().range(PREFIX + sessionId + ":contexts", 0, -1);
        if (contextJsons != null) {
            List<IAgentRuntimeContext> contexts = contextJsons.stream()
                    .map(s -> JSON.parseObject(s, AgentRuntimeContext.class))
                    .collect(Collectors.toList());
            session.setAgentRuntimeContexts(contexts);
        }

        return session;
    }

    @Override
    public List<IAgentSession> selectByUserId(String userId) {
        // 1. 从 Redis 中查用户会话列表（假设 Redis 里有 userId -> List<sessionId> 的映射）
        List<String> sessionIds = redisTemplate.opsForList().range(PREFIX + "user:" + userId, 0, -1);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<IAgentSession> result = new ArrayList<>();

        for (String sessionId : sessionIds) {
            String json = redisTemplate.opsForValue().get(PREFIX + sessionId);
            if (json == null) {
                continue; // 缓存未命中，跳过或可选择去数据库补充
            }

            AgentSession session = JSON.parseObject(json, AgentSession.class);

            // 读取 Items
            List<String> itemJsons = redisTemplate.opsForList().range(PREFIX + sessionId + ":items", 0, -1);
            if (itemJsons != null) {
                List<IAgentSessionItem> items = itemJsons.stream()
                        .map(s -> JSON.parseObject(s, AgentSessionItem.class))
                        .collect(Collectors.toList());
                session.setAgentSessionItems(items);
            }

            // 读取 Contexts
            List<String> contextJsons = redisTemplate.opsForList().range(PREFIX + sessionId + ":contexts", 0, -1);
            if (contextJsons != null) {
                List<IAgentRuntimeContext> contexts = contextJsons.stream()
                        .map(s -> JSON.parseObject(s, AgentRuntimeContext.class))
                        .collect(Collectors.toList());
                session.setAgentRuntimeContexts(contexts);
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

        // 保存 Session 主数据
        redisTemplate.opsForValue().set(PREFIX + session.getAgentSessionId(), JSON.toJSONString(session));

        // 保存 Items
        String itemsKey = PREFIX + session.getAgentSessionId() + ":items";
        redisTemplate.delete(itemsKey); // 清空旧数据
        if (session.getAgentSessionItems() != null && !session.getAgentSessionItems().isEmpty()) {
            List<String> itemJsons = session.getAgentSessionItems().stream()
                    .map(JSON::toJSONString)
                    .collect(Collectors.toList());
            redisTemplate.opsForList().rightPushAll(itemsKey, itemJsons);
        }

        // 保存 Contexts
        String ctxKey = PREFIX + session.getAgentSessionId() + ":contexts";
        redisTemplate.delete(ctxKey);
        if (session.getAgentRuntimeContexts() != null && !session.getAgentRuntimeContexts().isEmpty()) {
            List<String> ctxJsons = session.getAgentRuntimeContexts().stream()
                    .map(JSON::toJSONString)
                    .collect(Collectors.toList());
            redisTemplate.opsForList().rightPushAll(ctxKey, ctxJsons);
        }
    }

    @Override
    public void delete(String sessionId) {
        redisTemplate.delete(PREFIX + sessionId);
        redisTemplate.delete(PREFIX + sessionId + ":items");
        redisTemplate.delete(PREFIX + sessionId + ":contexts");
    }

    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession == null || sessionItem == null) {
            return;
        }

        String key = PREFIX + agentSession.getAgentSessionId() + ":items";
        redisTemplate.opsForList().rightPush(key, JSON.toJSONString(sessionItem));
    }

    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null)
            return;

        String key = PREFIX + agentSession.getAgentSessionId() + ":contexts";
        redisTemplate.opsForList().rightPush(key, JSON.toJSONString(runtimeContext));
    }
}
