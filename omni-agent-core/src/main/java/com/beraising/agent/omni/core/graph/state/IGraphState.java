package com.beraising.agent.omni.core.graph.state;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategy;
public interface IGraphState {

    /**
     * 获取当前状态对象
     *
     * @return OverAllState 当前状态对象
     */
    OverAllState getState();

    /**
     * 设置状态对象
     *
     * @param state 要设置的状态对象
     */
    void setState(OverAllState state);

    /**
     * 获取状态键值映射关系
     *
     * @return HashMap<String, KeyStrategy> 状态键值与策略的映射关系
     */
    HashMap<String, KeyStrategy> getStateKeys();

    /**
     * 获取代理会话ID
     *
     * @return String 代理会话ID，如果不存在则返回空字符串
     */
    default String getAgentSessionID() {
        return getState().value(IGraphState.getAgentSessionIDKey(), "");
    }

    /**
     * 获取代理运行时上下文ID
     *
     * @return String 代理运行时上下文ID，如果不存在则返回空字符串
     */
    default String getAgentRuntimeContextID() {
        return getState().value(IGraphState.getAgentRuntimeContextIDKey(), "");
    }

    /**
     * 获取更新后的会话ID状态
     *
     * @param <T> 泛型参数，继承自IGraphState
     * @param value 新的会话ID值
     * @return IUpdatedGraphState<T> 包含更新后会话ID的图状态对象
     */
    default <T extends IGraphState> IUpdatedGraphState<T> getUpdatedSessionID(String value) {
        return () -> {
            Map<String, Object> result = new HashMap<>();
            result.put(IGraphState.getAgentSessionIDKey(), value);
            return result;
        };
    }

    /**
     * 获取默认状态键值映射关系
     * 为代理会话ID和运行时上下文ID设置替换策略
     *
     * @return HashMap<String, KeyStrategy> 默认状态键值与策略的映射关系
     */
    static HashMap<String, KeyStrategy> getDefaultStateKeys() {
        HashMap<String, KeyStrategy> stateKeys = new HashMap<>();
        stateKeys.put(IGraphState.getAgentSessionIDKey(), new ReplaceStrategy());
        stateKeys.put(IGraphState.getAgentRuntimeContextIDKey(), new ReplaceStrategy());
        return stateKeys;
    }

    /**
     * 获取代理会话ID的键名
     *
     * @return String 代理会话ID的键名："session_id"
     */
    static String getAgentSessionIDKey() {
        return "session_id";
    }

    /**
     * 获取代理运行时上下文ID的键名
     *
     * @return String 代理运行时上下文ID的键名："runtime_context_id"
     */
    static String getAgentRuntimeContextIDKey() {
        return "runtime_context_id";
    }
}

