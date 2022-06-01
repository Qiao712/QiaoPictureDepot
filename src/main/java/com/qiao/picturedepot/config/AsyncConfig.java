package com.qiao.picturedepot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 设置任务执行器(线程池)
 */
@Configuration
public class AsyncConfig {

    /**
     * 定义处理图片文件的线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor pictureTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1024);
        executor.setThreadNamePrefix("pictureTaskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
