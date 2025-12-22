package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EAgentRequestType;
import com.beraising.agent.omni.core.event.IAgentRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AgentRequest类用于表示代理请求信息
 * 包含请求类型和请求数据，并提供复制功能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest implements IAgentRequest {
    /**
     * 请求类型枚举值
     */
    private EAgentRequestType requestType;

    /**
     * 请求数据字符串
     */
    private String requestData;

    /**
     * 创建当前对象的副本
     * 使用建造者模式构建新的AgentRequest实例，保持请求类型和请求数据一致
     *
     * @return 返回新的IAgentRequest接口实例，包含相同的请求类型和数据
     */
    @Override
    public IAgentRequest copy() {

        return AgentRequest.builder()
                .requestType(requestType)
                .requestData(requestData)
                .build();
    }

}
