package com.beraising.agent.omni.core.session;

/**
 * IAgentSessionUser接口定义了代理会话用户的相关操作
 * 该接口用于获取用户的唯一标识符
 */
public interface IAgentSessionUser {

    /**
     * 获取用户的唯一标识符
     *
     * @return 返回用户的ID字符串，用于唯一标识一个用户
     */
    String getUserId();
}

