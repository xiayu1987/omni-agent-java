package com.beraising.agent.omni.core.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.stereotype.Component;

import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Primary;

@Component
@Primary
public final class InMemoryChatMemoryRepository implements ChatMemoryRepository {
    Map<String, List<Message>> chatMemoryStore = new ConcurrentHashMap<>();

    public InMemoryChatMemoryRepository() {
    }

    public List<String> findConversationIds() {
        return new ArrayList<>(this.chatMemoryStore.keySet());
    }

    public List<Message> findByConversationId(String conversationId) {
        List<Message> messages = this.chatMemoryStore.get(conversationId);
        return messages != null ? new ArrayList<>(messages) : List.of();
    }

    public void saveAll(String conversationId, List<Message> messages) {
        this.chatMemoryStore.put(conversationId, messages);
    }

    public void deleteByConversationId(String conversationId) {
        this.chatMemoryStore.remove(conversationId);
    }
}
