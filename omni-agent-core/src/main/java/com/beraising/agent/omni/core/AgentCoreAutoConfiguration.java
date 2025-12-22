package com.beraising.agent.omni.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.beraising.agent.omni.core.session.config.AgentSessionProperties;

/**
 * AgentCoreAutoConfiguration 类用于自动配置代理核心功能。
 * 该类通过 Spring Boot 的自动配置机制，启用并配置代理会话相关的属性。
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>启用配置属性绑定，将 application.properties 或 application.yml 中的配置映射到 AgentSessionProperties 类</li>
 * </ul>
 *
 * @author [作者名]
 * @since [版本号]
 */
@AutoConfiguration
@EnableConfigurationProperties({
        AgentSessionProperties.class
})
public class AgentCoreAutoConfiguration {

}
