package com.beraising.agent.omni.core.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;

@Component
public class AgentContext {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatMemoryRepository memoryRepository;
    private final AgentRegistry agentRegistry;

    private final int MAX_MESSAGES = 100;

    public AgentContext(AgentRegistry agentRegistry, ChatClient.Builder chatClientBuilder,
            ChatMemoryRepository memoryRepository) {
        this.agentRegistry = agentRegistry;
        this.chatClientBuilder = chatClientBuilder;
        this.memoryRepository = memoryRepository;

        this.chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(

                        MessageWindowChatMemory.builder()
                                .chatMemoryRepository(this.memoryRepository)
                                .maxMessages(MAX_MESSAGES)
                                .build()

                )
                        .build(),
                        new SimpleLoggerAdvisor());
    }

    public AgentRegistry getAgentRegistry() {
        return agentRegistry;
    }

    public ChatClient.Builder getChatClientBuilder() {
        return chatClientBuilder;
    }

    public ChatMemoryRepository getMemoryRepository() {
        return memoryRepository;
    }

}