package com.allure.rap.roze.momory;

import com.allure.rap.roze.serializer.AiMessageDeserializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MemoryChatMemoryStore implements ChatMemoryStore {

    // Redis中存储聊天消息的key前缀
    private static final String CHAT_MESSAGES_KEY_PREFIX = "chat:memoryId:";

    // 聊天记录的过期时间，单位：小时
    private static final long EXPIRATION_HOURS = 24;

    // 配置ObjectMapper并注册自定义反序列化器
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 注册AiMessage的自定义反序列化器
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AiMessage.class, new AiMessageDeserializer());
        objectMapper.registerModule(module);

        // 基础配置
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .activateDefaultTyping(
                        LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
                )
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 添加getter方法，供外部获取RedisTemplate
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    @Override
    public  List<ChatMessage> getMessages(Object memoryId){
        String key = getRedisKey(memoryId);
        List<ChatMessage> messages = new ArrayList<>();
        try {
            // 1. 从Redis列表中获取所有元素（0表示第一个元素，-1表示最后一个元素，即全量获取）
            List<Object> redisList = redisTemplate.opsForList().range(key, 0, -1);

            // 2. 处理空列表场景（无数据时返回空列表）
            if (redisList == null || redisList.isEmpty()) {
                return messages;
            }

            // 3. 逐个反序列化Redis中的JSON字符串为ChatMessage对象
            for (Object redisItem : redisList) {
                // 确保元素是字符串类型（存储时已序列化为JSON字符串）
                String jsonMessage = redisItem.toString();
                // 反序列化（自动识别子类类型，如AiMessage）
                ChatMessage message = objectMapper.readValue(jsonMessage, ChatMessage.class);
                messages.add(message);
            }
        } catch (Exception e) {
            // 捕获序列化或Redis操作异常，包装后抛出
            throw new RuntimeException("获取聊天消息失败，memoryId: " + memoryId, e);
        }
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages){
        String key = getRedisKey(memoryId);
        try {
            for (ChatMessage message : messages) {
                String json = objectMapper.writeValueAsString(message);
                redisTemplate.opsForList().leftPush(key, json);
                redisTemplate.expire(key, EXPIRATION_HOURS, TimeUnit.HOURS);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMessages(Object memoryId){
        String key = getRedisKey(memoryId);
        redisTemplate.delete(key);
    }


    /**
     * 生成Redis的key
     */
    private String getRedisKey(Object memoryId) {
        return CHAT_MESSAGES_KEY_PREFIX + memoryId.toString();
    }
}
