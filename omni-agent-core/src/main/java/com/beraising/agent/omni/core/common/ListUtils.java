package com.beraising.agent.omni.core.common;

import java.util.List;

public class ListUtils {
    public static <T> T lastOf(List<T> list) {
        return (list != null && !list.isEmpty()) ? list.get(list.size() - 1) : null;
    }
}
