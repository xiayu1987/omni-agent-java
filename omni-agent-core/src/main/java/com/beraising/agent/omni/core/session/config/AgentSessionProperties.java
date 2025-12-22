package com.beraising.agent.omni.core.session.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Agent会话配置属性类
 * 用于配置agent会话的相关属性，通过@ConfigurationProperties注解自动绑定application.yml中以"agent.session"为前缀的配置项
 */
@ConfigurationProperties(prefix = "agent.session")
@Data
public class AgentSessionProperties {

    /**
     * 存储类型：memory | db | redis
     */
    private StorageType storeType = StorageType.MEMORY;

    /**
     * 存储类型枚举
     * 定义了会话数据的存储方式
     */
    public enum StorageType {
        /**
         * 内存存储
         */
        MEMORY,
        /**
         * 数据库存储
         */
        DB,
        /**
         * Redis存储
         */
        REDIS
    }
}
