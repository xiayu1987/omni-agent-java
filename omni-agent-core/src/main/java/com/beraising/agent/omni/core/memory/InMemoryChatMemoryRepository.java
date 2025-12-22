package com.beraising.agent.omni.core.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.stereotype.Component;

import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Primary;
/**
 * 基于内存的聊天记忆存储仓库实现类
 * 使用ConcurrentHashMap作为存储介质，提供聊天对话的增删改查功能
 */
@Component
@Primary
public final class InMemoryChatMemoryRepository implements ChatMemoryRepository {
    Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();

    public InMemoryChatMemoryRepository() {
    }

    /**
     * 查找所有对话ID
     *
     * @return 包含所有对话ID的字符串列表
     */
    public List<String> findConversationIds() {
        return new ArrayList<>(this.chatMemoryStore.keySet());
    }

    /**
     * 根据对话ID查找消息列表
     *
     * @param conversationId 对话唯一标识符
     * @return 对应对话的消息列表，如果不存在则返回空列表
     */
    public List<Message> findByConversationId(String conversationId) {
        List<Message> messages = this.chatMemoryStore.get(conversationId);
        return messages != null ? new ArrayList<>(messages) : List.of();
    }

    /**
     * 保存指定对话ID的全部消息
     *
     * @param conversationId 对话唯一标识符
     * @param messages 要保存的消息列表
     */
    public void saveAll(String conversationId, List<Message> messages) {
        this.chatMemoryStore.put(conversationId, messages);
    }

    /**
     * 根据对话ID删除对话记录
     *
     * @param conversationId 要删除的对话唯一标识符
     */
    public void deleteByConversationId(String conversationId) {
        this.chatMemoryStore.remove(conversationId);
    }
}

