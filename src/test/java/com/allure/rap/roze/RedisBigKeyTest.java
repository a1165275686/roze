package com.allure.rap.roze;


import com.allure.rap.roze.momory.MemoryChatMemoryStore;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RedisBigKeyTest {
    private static final String BIG_KEY_MEMORY_ID = "20252021";
    private static final int BIG_DATA_SIZE = 1000; // 大key数据量
    private static final int TEST_DURATION_SECONDS = 60; // 测试持续时间

    public static void main(String[] args) {
        // 启动Spring Boot应用，获取上下文
        ApplicationContext context = SpringApplication.run(RozeApplication.class, args);

        // 获取需要的Bean
        MemoryChatMemoryStore memoryStore = context.getBean(MemoryChatMemoryStore.class);

        // 准备大key测试数据
        List<ChatMessage> bigKeyMessages = prepareBigKeyData();
        log.info("准备完成大key测试数据，包含 {} 条消息", bigKeyMessages.size());

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 启动大key操作线程
        executor.submit(new BigKeyOperationTask(memoryStore, bigKeyMessages));

        // 启动普通操作线程
        //executor.submit(new NormalOperationTask(memoryStore.getRedisTemplate()));

        // 运行指定时间后关闭测试
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(TEST_DURATION_SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("测试被中断", e);
        }

        // 关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        log.info("Redis大key测试完成");
        System.exit(0);
    }

    /**
     * 准备大key测试数据
     */
    private static List<ChatMessage> prepareBigKeyData() {
        String memoryId = "20252021";
        List<ChatMessage> messages = new ArrayList<>();
        List<ToolExecutionRequest> toolExecutionRequests = new ArrayList<>();
        int num = 500;
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
        return messages;
    }

    /**
     * 大key操作线程任务
     */
    static class BigKeyOperationTask implements Runnable {
        private final MemoryChatMemoryStore memoryStore;
        private final List<ChatMessage> bigKeyMessages;
        private int iteration = 0;

        private static  final AtomicBoolean updateCount = new AtomicBoolean(false);
        public BigKeyOperationTask(MemoryChatMemoryStore memoryStore, List<ChatMessage> bigKeyMessages) {
            this.memoryStore = memoryStore;
            this.bigKeyMessages = bigKeyMessages;
        }

        @Override
        public void run() {
            log.info("大key操作线程启动");
            long updateTime;
            while (!Thread.currentThread().isInterrupted()) {
                iteration++;
                try {
                    // 大key更新操作
                    long startTime = System.currentTimeMillis();
                    memoryStore.updateMessages(BIG_KEY_MEMORY_ID, bigKeyMessages);
                    updateTime = System.currentTimeMillis() - startTime;
                    log.info("更新耗时: {}ms ",updateTime);
                    updateCount.set(true);
                    if(!updateCount.get()){
                        // 大key更新操作
                         startTime = System.currentTimeMillis();
                        memoryStore.updateMessages(BIG_KEY_MEMORY_ID, bigKeyMessages);
                         updateTime = System.currentTimeMillis() - startTime;
                        updateCount.set(true);
                    }else{
                        continue;
                    }

                    // 大key读取操作
                     startTime = System.currentTimeMillis();
                    List<ChatMessage> messages = memoryStore.getMessages(BIG_KEY_MEMORY_ID);
                    long readTime = System.currentTimeMillis() - startTime;

                    log.info("大key操作[{}] - 更新耗时: {}ms, 读取耗时: {}ms, 消息数量: {}",
                            iteration, updateTime, readTime, messages.size());

                    // 操作间隔，避免过于频繁
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("大key操作线程被中断");
                    break;
                } catch (Exception e) {
                    log.error("大key操作出错", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 普通操作线程任务
     */
    static class NormalOperationTask implements Runnable {
        private final RedisTemplate<String, Object> redisTemplate;
        private final Random random = new Random();
        private int successCount = 0;
        private int failCount = 0;

        public NormalOperationTask(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public void run() {
            log.info("普通操作线程启动");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 生成随机的普通key
                    String normalKey = "normal_key_" + random.nextInt(1000);

                    // 普通写入操作
                    long startTime = System.currentTimeMillis();
                    redisTemplate.opsForValue().set(normalKey, "普通value内容_" + System.currentTimeMillis(),
                            5, TimeUnit.MINUTES);
                    long writeTime = System.currentTimeMillis() - startTime;

                    // 普通读取操作
                    startTime = System.currentTimeMillis();
                    Object value = redisTemplate.opsForValue().get(normalKey);
                    long readTime = System.currentTimeMillis() - startTime;

                    successCount++;
                    log.info("普通操作[成功:{} 失败:{}] - key: {}, 写入耗时: {}ms, 读取耗时: {}ms",
                            successCount, failCount, normalKey, writeTime, readTime);

                    // 更频繁地执行普通操作，模拟真实场景
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("普通操作线程被中断");
                    break;
                } catch (Exception e) {
                    failCount++;
                    log.error("普通操作出错", e);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}
