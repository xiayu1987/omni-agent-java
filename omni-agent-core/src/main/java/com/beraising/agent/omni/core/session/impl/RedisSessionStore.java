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
/**
 * RedisSessionStore 是一个基于 Redis 实现的会话存储组件。
 * 它实现了 ISessionStore 接口，用于管理 AgentSession、AgentSessionItem 和 AgentRuntimeContext 数据。
 */
@Component("redis")
public class RedisSessionStore implements ISessionStore {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "agent:session:";

    /**
     * 构造方法，注入 Redis 操作模板。
     *
     * @param redisTemplate Redis 字符串操作模板对象
     */
    public RedisSessionStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据会话 ID 查询完整的 AgentSession 对象。
     * 包括主信息、关联的 Items 列表以及 Contexts 列表。
     *
     * @param sessionId 会话唯一标识符
     * @return 返回对应的 IAgentSession 对象；如果不存在则返回 null
     */
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

    /**
     * 根据用户 ID 查询该用户的所有会话记录。
     * 需要先获取用户的会话 ID 列表，再逐个查询具体会话内容。
     *
     * @param userId 用户唯一标识符
     * @return 返回该用户下所有有效的 IAgentSession 列表
     */
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

    /**
     * 将整个会话及其相关 Items 和 Contexts 存储到 Redis 中。
     * 先清空旧数据后重新插入新数据。
     *
     * @param session 要保存的会话对象
     */
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

    /**
     * 删除指定会话的所有相关信息：包括主数据、Items 和 Contexts。
     *
     * @param sessionId 要删除的会话 ID
     */
    @Override
    public void delete(String sessionId) {
        redisTemplate.delete(PREFIX + sessionId);
        redisTemplate.delete(PREFIX + sessionId + ":items");
        redisTemplate.delete(PREFIX + sessionId + ":contexts");
    }

    /**
     * 向指定会话中追加一个新的 SessionItem。
     *
     * @param agentSession 所属会话对象
     * @param sessionItem  要添加的 Item 对象
     */
    @Override
    public void addSessionItem(IAgentSession agentSession, IAgentSessionItem sessionItem) {
        if (agentSession == null || sessionItem == null) {
            return;
        }

        String key = PREFIX + agentSession.getAgentSessionId() + ":items";
        redisTemplate.opsForList().rightPush(key, JSON.toJSONString(sessionItem));
    }

    /**
     * 向指定会话中添加一个新的运行时上下文（AgentRuntimeContext）。
     * 使用 Hash 结构进行存储以便后续更新。
     *
     * @param agentSession   所属会话对象
     * @param runtimeContext 要添加的运行时上下文对象
     */
    @Override
    public void addAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        if (agentSession == null || runtimeContext == null) {
            return;
        }
        String key = PREFIX + agentSession.getAgentSessionId() + ":contexts";
        redisTemplate.opsForHash().put(key, runtimeContext.getAgentRuntimeContextId(),
                JSON.toJSONString(runtimeContext));
    }

    /**
     * 更新指定会话中的某个运行时上下文。
     * 因为 Redis Hash 的 put 方法支持覆盖已有字段，因此可以与 add 复用逻辑。
     *
     * @param agentSession   所属会话对象
     * @param runtimeContext 要更新的运行时上下文对象
     */
    @Override
    public void updateAgentRuntimeContext(IAgentSession agentSession, IAgentRuntimeContext runtimeContext) {
        // Hash 的 put 本身就是新增/覆盖，所以 add 和 update 可以复用
        addAgentRuntimeContext(agentSession, runtimeContext);
    }
}
