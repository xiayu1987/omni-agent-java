package com.beraising.agent.omni.core.event.impl;

import com.beraising.agent.omni.core.event.EAgentResponseType;
import com.beraising.agent.omni.core.event.IAgentResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent响应类，用于封装代理的响应信息
 * 实现了IAgentResponse接口，支持构建者模式和多种构造方式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse implements IAgentResponse {

    /**
     * 响应类型枚举值
     */
    private EAgentResponseType responseType;

    /**
     * 响应数据字符串
     */
    private String responseData;

    /**
     * 创建当前对象的副本
     * 使用构建者模式创建一个新的AgentResponse实例，包含相同的响应类型和响应数据
     *
     * @return 返回新的IAgentResponse实例，包含与当前对象相同的数据
     */
    @Override
    public IAgentResponse copy() {

        return AgentResponse.builder()
                .responseType(responseType)
                .responseData(responseData)
                .build();
    }

}
