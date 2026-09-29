package com.necom.config.chatbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

    // Pool riêng cho chatbot để các lời gọi LLM chậm không chiếm thread của phần còn lại
    @Bean(name = "chatbotExecutor")
    public Executor chatbotExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("chatbot-");
        executor.initialize();
        return executor;
    }


    // Scheduler riêng cho @Scheduled (làm mới chỉ mục tìm kiếm). Không có bean này, Spring dùng scheduler
    // của STOMP broker và tác vụ nặng sẽ chặn heartbeat WebSocket.
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-");
        scheduler.initialize();
        return scheduler;
    }

}
