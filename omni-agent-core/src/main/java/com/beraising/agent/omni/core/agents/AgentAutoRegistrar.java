package com.beraising.agent.omni.core.agents;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Agent自动注册器
 *
 * 该类负责在应用程序启动时自动注册所有IAgent实例到AgentRegistry中。
 * 通过实现ApplicationRunner接口，确保在Spring应用上下文完全加载后执行注册逻辑。
 */
@Component
@Order(0)
public class AgentAutoRegistrar implements ApplicationRunner {

    private final List<IAgent> agents;
    private final AgentRegistry registry;

    /**
     * 构造函数，初始化Agent自动注册器
     *
     * @param agents 需要注册的IAgent实例列表
     * @param registry Agent注册表，用于管理所有已注册的Agent
     */
    public AgentAutoRegistrar(List<IAgent> agents, AgentRegistry registry) {
        this.agents = agents;
        this.registry = registry;

        // 在构造函数中预先注册所有Agent实例
        for (IAgent agent : this.agents) {
            this.registry.register(agent);
        }
    }

    /**
     * 应用程序运行方法，在Spring应用上下文完全加载后执行
     *
     * @param args 应用程序启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // for (IAgent agent : agents) {
        //     registry.register(agent);
        // }
    }
}
