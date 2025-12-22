package com.beraising.agent.omni.core.session;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
/**
 * 表初始化器，用于在数据库中创建会话相关的表结构
 * 该类仅在配置属性 agent.session.storeType=db 时生效
 */
@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@Component
public class TableInitializer {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 构造函数
     * @param jdbcTemplate JDBC模板对象，用于执行SQL语句
     */
    public TableInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 初始化方法，在对象创建完成后自动执行
     * 负责创建三个数据库表：t_agent_session、t_agent_session_item 和 t_agent_runtime_context
     */
    @PostConstruct
    public void init() {
        // 创建代理会话表，存储会话基本信息
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_session (
                        session_id VARCHAR(64) PRIMARY KEY,
                        parent_session_id VARCHAR(64),
                        user_id VARCHAR(64),
                        user_type INT,
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);

        // 创建代理会话项表，存储会话中的具体数据项
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_session_item (
                        id BIGINT PRIMARY KEY,
                        session_id VARCHAR(64),
                        item_data TEXT,
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);

        // 创建代理运行时上下文表，存储会话的运行时上下文信息
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_runtime_context (
                        id VARCHAR(64) PRIMARY KEY,
                        session_id VARCHAR(64),
                        context_data TEXT,
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);
    }
}

