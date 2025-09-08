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
                        s_session_id VARCHAR(64) PRIMARY KEY,
                        s_user_id VARCHAR(64),
                        t_update_time DATETIME,
                        t_create_time DATETIME
                    )
                """);

        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_session_item (
                        n_id BIGINT PRIMARY KEY,
                        s_session_id VARCHAR(64),
                        s_item_data TEXT,
                        t_update_time DATETIME,
                        t_create_time DATETIME
                    )
                """);

        jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS t_agent_runtime_context (
                        n_id BIGINT PRIMARY KEY,
                        s_session_id VARCHAR(64),
                        s_context_data TEXT,
                        t_update_time DATETIME,
                        t_create_time DATETIME
                    )
                """);
    }
}
