package com.beraising.agent.omni.core.exception;
/**
 * 业务异常类，用于处理业务逻辑中出现的异常情况
 * 继承自Exception类，是一个受检异常
 */
public class BusinessException extends Exception {

    /**
     * 构造一个带有指定详细消息的业务异常
     *
     * @param message 异常的详细信息，用于描述异常发生的原因
     */
    public BusinessException(String message) {
        super(message);
    }

}

