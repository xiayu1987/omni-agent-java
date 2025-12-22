package com.beraising.agent.omni.core.graph.state;

import com.alibaba.cloud.ai.graph.OverAllState;

/**
 * 图状态基类，实现IGraphState接口
 * 提供图状态的基本功能，包括状态的获取和设置
 */
public abstract class GraphStateBase implements IGraphState {

    private OverAllState state;

    /**
     * 获取当前状态
     *
     * @return OverAllState 当前的整体状态
     */
    public OverAllState getState() {
        return state;
    }

    /**
     * 设置当前状态
     *
     * @param state 要设置的整体状态
     */
    public void setState(OverAllState state) {
        this.state = state;
    }

}
