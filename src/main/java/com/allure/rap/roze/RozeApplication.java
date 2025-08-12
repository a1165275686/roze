package com.allure.rap.roze;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.allure.rap.roze.mapper")
public class RozeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RozeApplication.class, args);
    }

}
