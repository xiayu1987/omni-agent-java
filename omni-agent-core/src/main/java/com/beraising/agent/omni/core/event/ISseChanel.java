package com.beraising.agent.omni.core.event;

/**
 * ISseChanel接口定义了SSE（Server-Sent Events）通道的基本操作方法。
 * 该接口提供了向客户端发送事件、错误和完成信号的功能。
 */
public interface ISseChanel {

    /**
     * 尝试向客户端发送下一个事件数据。
     *
     * @param agentEvent 要发送的代理事件对象，包含事件的具体数据内容
     */
    void tryEmitNext(IAgentEvent agentEvent);

    /**
     * 尝试向客户端发送错误信息。
     *
     * @param error 要发送的异常或错误对象，用于通知客户端发生了错误
     */
    void tryEmitError(Throwable error);

    /**
     * 尝试向客户端发送完成信号。
     * 调用此方法表示事件流已经结束，客户端可以关闭连接。
     */
    void tryEmitComplete();

}

