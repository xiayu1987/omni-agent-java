package com.beraising.agent.omni.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.beraising.agent.omni.core.session.config.AgentSessionProperties;

@AutoConfiguration
@EnableConfigurationProperties({
        AgentSessionProperties.class
})
public class AgentCoreAutoConfiguration {

}