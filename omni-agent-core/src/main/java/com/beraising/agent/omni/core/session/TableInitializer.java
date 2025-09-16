package com.beraising.agent.omni.core.session;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@Component
public class TableInitializer {

    private final JdbcTemplate jdbcTemplate;

    public TableInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_session (
                        session_id VARCHAR(64) PRIMARY KEY,
                        user_id VARCHAR(64),
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);

        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_session_item (
                        id BIGINT PRIMARY KEY,
                        session_id VARCHAR(64),
                        item_data TEXT,
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);

        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_runtime_context (
                        id BIGINT PRIMARY KEY,
                        session_id VARCHAR(64),
                        context_data TEXT,
                        update_time DATETIME,
                        create_time DATETIME
                    )
                """);
    }
}
