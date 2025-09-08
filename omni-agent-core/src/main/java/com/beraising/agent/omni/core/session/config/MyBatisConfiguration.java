package com.beraising.agent.omni.core.session.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration
@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@MapperScan("com.beraising.agent.omni.core.session.mapper")
public class MyBatisConfiguration {

}