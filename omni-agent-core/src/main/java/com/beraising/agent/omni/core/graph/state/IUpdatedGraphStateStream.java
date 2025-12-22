package com.beraising.agent.omni.core.graph.state;

/**
 * IUpdatedGraphStateStream接口继承自IUpdatedGraphState接口，表示一个更新图状态流的泛型接口。
 * 该接口用于处理图状态的流式更新操作。
 *
 * @param <T> 泛型参数，表示图状态中存储的数据类型
 */
public interface IUpdatedGraphStateStream<T> extends IUpdatedGraphState<T> {

}
