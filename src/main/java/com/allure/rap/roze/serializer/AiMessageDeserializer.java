package com.allure.rap.roze.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 自定义反序列化器，告诉Jackson如何构造AiMessage
public class AiMessageDeserializer extends JsonDeserializer<AiMessage> {
    @Override
    public AiMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 从JSON中提取text字段
        String text = node.has("text") ? node.get("text").asText() : null;

        // 从JSON中提取toolExecutionRequests字段
        List<ToolExecutionRequest> toolRequests = new ArrayList<>();
        if (node.has("toolExecutionRequests") && node.get("toolExecutionRequests").isArray()) {
            for (JsonNode requestNode : node.get("toolExecutionRequests")) {
                // 解析每个ToolExecutionRequest（假设其结构包含id、name、arguments等字段）
                String id = requestNode.has("id") ? requestNode.get("id").asText() : null;
                String name = requestNode.has("name") ? requestNode.get("name").asText() : null;
                String arguments = requestNode.has("arguments") ? requestNode.get("arguments").asText() : null;

                // 构造ToolExecutionRequest（根据实际构造函数调整）
                ToolExecutionRequest request = ToolExecutionRequest.builder()
                        .id(id)
                        .name(name)
                        .arguments(arguments)
                        .build();
                toolRequests.add(request);
            }
        }

        // 根据提取的字段构造AiMessage（匹配其现有构造函数）
        if (text != null && !toolRequests.isEmpty()) {
            return new AiMessage(text, toolRequests);
        } else if (text != null) {
            return new AiMessage(text);
        } else if (!toolRequests.isEmpty()) {
            return new AiMessage(toolRequests);
        } else {
            throw new IOException("无法构造AiMessage：缺少text或toolExecutionRequests字段");
        }
    }
}