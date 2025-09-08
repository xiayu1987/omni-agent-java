package com.beraising.agent.omni.core.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "agent.session")
@Data
public class AgentSessionProperties {

    /**
     * 存储类型：memory | db | redis
     */
    private StorageType storeType = StorageType.MEMORY;

    public enum StorageType {
        MEMORY,
        DB,
        REDIS
    }
}
