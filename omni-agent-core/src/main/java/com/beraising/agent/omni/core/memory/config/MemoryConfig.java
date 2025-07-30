// package com.beraising.agent.omni.core.memory.config;

// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.datasource.DriverManagerDataSource;

// import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
// import com.alibaba.cloud.ai.memory.jdbc.SQLiteChatMemoryRepository;
// import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;

// @Configuration
// public class MemoryConfig {

//     @Value("${spring.ai.memory.redis.host:}")
//     private String redisHost;
//     @Value("${spring.ai.memory.redis.port:6379}")
//     private int redisPort;
//     @Value("${spring.ai.memory.redis.password:}")
//     private String redisPassword;
//     @Value("${spring.ai.memory.redis.timeout:5000}")
//     private int redisTimeout;

//     @Value("${spring.ai.chat.memory.repository.jdbc.mysql.jdbc-url:}")
//     private String mysqlJdbcUrl;
//     @Value("${spring.ai.chat.memory.repository.jdbc.mysql.username:}")
//     private String mysqlUsername;
//     @Value("${spring.ai.chat.memory.repository.jdbc.mysql.password:}")
//     private String mysqlPassword;
//     @Value("${spring.ai.chat.memory.repository.jdbc.mysql.driver-class-name:}")
//     private String mysqlDriverClassName;

//     @Bean
//     @ConditionalOnProperty(prefix = "spring.ai.chat.memory.repository.jdbc", name = "enabled", havingValue = "true", matchIfMissing = false)
//     public SQLiteChatMemoryRepository sqliteChatMemoryRepository() {
//         DriverManagerDataSource dataSource = new DriverManagerDataSource();
//         dataSource.setDriverClassName("org.sqlite.JDBC");
//         dataSource.setUrl("jdbc:sqlite:spring-ai-alibaba-chat-memory-example/src/main/resources/chat-memory.db");
//         JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//         return SQLiteChatMemoryRepository.sqliteBuilder()
//                 .jdbcTemplate(jdbcTemplate)
//                 .build();
//     }

//     @Bean
//     @ConditionalOnProperty(prefix = "spring.ai.chat.memory.repository.jdbc.mysql", name = "jdbc-url")
//     public MysqlChatMemoryRepository mysqlChatMemoryRepository() {
//         DriverManagerDataSource dataSource = new DriverManagerDataSource();
//         dataSource.setDriverClassName(mysqlDriverClassName);
//         dataSource.setUrl(mysqlJdbcUrl);
//         dataSource.setUsername(mysqlUsername);
//         dataSource.setPassword(mysqlPassword);
//         JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//         return MysqlChatMemoryRepository.mysqlBuilder()
//                 .jdbcTemplate(jdbcTemplate)
//                 .build();
//     }

//     @Bean
//     @ConditionalOnProperty(prefix = "spring.ai.memory.redis", name = "host")
//     public RedisChatMemoryRepository redisChatMemoryRepository() {
//         return RedisChatMemoryRepository.builder()
//                 .host(redisHost)
//                 .port(redisPort)
//                 // 若没有设置密码则注释该项
//                 // .password(redisPassword)
//                 .timeout(redisTimeout)
//                 .build();
//     }
// }
