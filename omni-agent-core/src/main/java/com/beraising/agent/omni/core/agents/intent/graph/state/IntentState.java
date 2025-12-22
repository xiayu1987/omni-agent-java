package com.beraising.agent.omni.core.agents.intent.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.beraising.agent.omni.core.context.IAgentRuntimeContext;
import com.beraising.agent.omni.core.event.IAgentEvent;
import com.beraising.agent.omni.core.graph.state.GraphStateBase;
import com.beraising.agent.omni.core.graph.state.IUpdatedGraphStateValue;
/**
 * IntentState类用于管理意图识别相关的状态信息
 * 继承自GraphStateBase，提供用户输入和意图结果的状态管理功能
 */
public class IntentState extends GraphStateBase {

    /**
     * 获取状态键策略映射表
     * 定义了"user_input"和"intent_result"两个状态键，都使用ReplaceStrategy策略
     *
     * @return 包含状态键和对应策略的HashMap映射
     */
    @Override
    public HashMap<String, KeyStrategy> getStateKeys() {
        HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();

        keyStrategyHashMap.put("user_input", new ReplaceStrategy());
        keyStrategyHashMap.put("intent_result", new ReplaceStrategy());
        return keyStrategyHashMap;
    }

    /**
     * 将用户输入数据放入输入映射中
     * 从agentEvent中获取请求数据，并以"user_input"为键存入input映射
     *
     * @param input 输入数据映射
     * @param agentRuntimeContext 代理运行时上下文
     * @param agentEvent 代理事件对象
     */
    public void putUserInput(Map<String, Object> input, IAgentRuntimeContext agentRuntimeContext,
            IAgentEvent agentEvent) {
        input.put("user_input", agentEvent.getAgentRequest().getRequestData());
    }

    /**
     * 获取用户输入内容
     * 从状态中获取"user_input"键对应的值，如果不存在则返回空字符串
     *
     * @return 用户输入的字符串内容
     */
    public String getUserInput() {
        return getState().value("user_input", "");
    }

    /**
     * 获取意图识别结果
     * 从状态中获取"intent_result"键对应的值，如果不存在则返回空字符串
     *
     * @return 意图识别结果的字符串内容
     */
    public String getIntentResult() {
        return getState().value("intent_result", "");
    }

    /**
     * 创建更新意图结果的状态值对象
     * 返回一个IUpdatedGraphStateValue接口实现，用于更新intent_result状态值
     *
     * @param value 要设置的意图结果值
     * @return 包含新意图结果值的更新状态对象
     */
    public IUpdatedGraphStateValue<IntentState> getUpdatedIntentResult(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put("intent_result", value);
            return result;
        };
    }

}

