package com.allure.rap.roze.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    // 自定义 RedisTemplate，使用 JSON 序列化（推荐）
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 字符串键序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // JSON 值序列化器（支持对象自动序列化/反序列化）
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(stringSerializer);       // 键用字符串序列化
        template.setValueSerializer(jsonSerializer);       // 值用 JSON 序列化
        template.setHashKeySerializer(stringSerializer);   // 哈希键用字符串序列化
        template.setHashValueSerializer(jsonSerializer);   // 哈希值用 JSON 序列化
        template.afterPropertiesSet();
        return template;
    }
}
