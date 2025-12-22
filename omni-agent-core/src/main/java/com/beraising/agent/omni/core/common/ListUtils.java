package com.beraising.agent.omni.core.common;

import java.util.List;
import java.util.Optional;

/**
 * 列表工具类，提供对列表操作的通用方法
 */
public class ListUtils {
    /**
     * 获取列表的最后一个元素
     *
     * @param <T> 列表元素的类型
     * @param list 要获取最后一个元素的列表
     * @return 如果列表不为空则返回最后一个元素，否则返回null
     */
    public static <T> T lastOf(List<T> list) {
        // 检查列表是否为空，避免空指针异常和索引越界异常
        return (list != null && !list.isEmpty()) ? list.get(list.size() - 1) : null;
    }
    public static <T> Optional<T> optionalLastOf(List<T> list) {
        // 检查列表是否为空，避免空指针异常和索引越界异常
        return (list != null && !list.isEmpty()) ? Optional.of(list.get(list.size() - 1)) : Optional.empty();
    }

}
