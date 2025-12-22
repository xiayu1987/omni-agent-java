package com.beraising.agent.omni.core.session.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * MyBatis配置类
 *
 * 该配置类用于配置基于数据库的会话存储功能，只有当配置属性agent.session.storeType=db时才会生效
 *
 * 主要功能：
 * 1. 启用自动配置功能
 * 2. 根据配置条件决定是否启用该配置
 * 3. 扫描指定包下的MyBatis Mapper接口
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "agent.session", name = "storeType", havingValue = "db")
@MapperScan("com.beraising.agent.omni.core.session.mapper")
public class MyBatisConfiguration {

}
