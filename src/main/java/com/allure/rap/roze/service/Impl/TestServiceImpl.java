package com.allure.rap.roze.service.Impl;

import com.allure.rap.roze.RozeApplication;
import com.allure.rap.roze.momory.MemoryChatMemoryStore;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestServiceImpl {
    @Resource
    private  MemoryChatMemoryStore memoryChatMemoryStore;


    public static void main(String[] args) {
        String memoryId = "20252021";
        List<ChatMessage> messages = new ArrayList<>();
        List<ToolExecutionRequest> toolExecutionRequests = new ArrayList<>();
        int num = 1000;
        for (int i = 0; i < num ; i++) {
            ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                    .id(memoryId)
                    .name("test")
                    .arguments("test")
                    .build();
            toolExecutionRequests.add(toolExecutionRequest);
        }
        for (int i = 0; i < num ; i++) {
            AiMessage chatMessage = AiMessage.builder()
                    .text("请描述太阳")
                    .toolExecutionRequests(toolExecutionRequests)
                    .build();
            messages.add(chatMessage);
        }
        // 启动Spring Boot应用，加载主配置类
        ApplicationContext context = SpringApplication.run(RozeApplication.class, args);

        // 从容器中手动获取实例
        MemoryChatMemoryStore memoryChatMemoryStore = context.getBean(MemoryChatMemoryStore.class);

        long startTime = System.currentTimeMillis();
        memoryChatMemoryStore.updateMessages(memoryId, messages);
        long endTime = System.currentTimeMillis();
        log.info("更新花费时间:{}ms",endTime-startTime);

         startTime = System.currentTimeMillis();
        List<ChatMessage> messages1 = memoryChatMemoryStore.getMessages(memoryId);
         endTime = System.currentTimeMillis();
        log.info("读取花费时间:{}ms",endTime-startTime);
    }


}
