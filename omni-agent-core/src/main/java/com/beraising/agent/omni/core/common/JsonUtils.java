package com.beraising.agent.omni.core.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 从文本中提取第一个合法的 JSON 字符串
     *
     * @param text 包含 JSON 的原始文本
     * @return 提取到的 JSON，如果没有找到返回 null
     */
    public static String extractFirstJson(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        int start = -1;
        int braceCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{') {
                if (braceCount == 0) {
                    start = i; // 记录起点
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    String candidate = text.substring(start, i + 1).trim();
                    if (isValidJson(candidate)) {
                        return candidate; // 返回第一个合法 JSON
                    }
                }
            }
        }

        return null;
    }

    /**
     * 校验字符串是否为合法 JSON
     */
    private static boolean isValidJson(String str) {
        try {
            mapper.readTree(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 直接解析 JSON 成 Jackson 的 JsonNode
     */
    public static JsonNode parseJsonNode(String text) {
        String jsonStr = extractFirstJson(text);
        if (jsonStr == null) {
            return null;
        }
        try {
            return mapper.readTree(jsonStr);
        } catch (Exception e) {
            return null;
        }
    }
}
