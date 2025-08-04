package com.beraising.agent.omni.core.context.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Component;

import com.beraising.agent.omni.core.agents.AgentRegistry;
import com.beraising.agent.omni.core.agents.IAgentEngine;
import com.beraising.agent.omni.core.context.IAgentStaticContext;

@Component
public class AgentStaticContext implements IAgentStaticContext {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatMemoryRepository memoryRepository;
    private final AgentRegistry agentRegistry;
    private final IAgentEngine agentEngine;

    private final int MAX_MESSAGES = 100;

    public AgentStaticContext(IAgentEngine agentEngine, AgentRegistry agentRegistry,
            ChatClient.Builder chatClientBuilder,
            ChatMemoryRepository memoryRepository) {
        this.agentRegistry = agentRegistry;
        this.chatClientBuilder = chatClientBuilder;
        this.memoryRepository = memoryRepository;
        this.agentEngine = agentEngine;

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

    @Override
    public AgentRegistry getAgentRegistry() {
        return agentRegistry;
    }

    @Override
    public ChatClient.Builder getChatClientBuilder() {
        return chatClientBuilder;
    }

    @Override
    public ChatMemoryRepository getMemoryRepository() {
        return memoryRepository;
    }

    @Override
    public IAgentEngine getAgentEngine() {
        return agentEngine;
    }

}