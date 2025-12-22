package com.beraising.agent.omni.core.graph.state;

import java.util.Map;

/**
 * 图状态更新接口
 *
 * @param <T> 泛型参数，表示图状态的类型
 */
public interface IUpdatedGraphState<T> {

    /**
     * 执行图状态更新操作
     *
     * @return 返回包含执行结果的键值对映射，其中键为字符串类型，值为任意对象类型
     */
    Map<String, Object> exec();

}
