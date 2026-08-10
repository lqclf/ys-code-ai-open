package com.eric.log.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 日志异步配置类
 * <p>
 * 配置日志记录专用的异步线程池，将日志入库操作从主业务线程中剥离，
 * 避免日志记录阻塞正常业务请求，提高系统吞吐量。
 * </p>
 *
 * @ClassName:  LogAsyncConfig
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Slf4j
@Configuration
@EnableAsync
public class LogAsyncConfig {

    /** 线程池核心线程数：始终保持活跃的线程数量 */
    private static final int CORE_POOL_SIZE = 5;

    /** 线程池最大线程数：核心线程满载后可创建的最大线程数量 */
    private static final int MAX_POOL_SIZE = 10;

    /** 线程池队列容量：核心线程满载后新任务的等待队列长度 */
    private static final int QUEUE_CAPACITY = 200;

    /** 线程名称前缀，便于问题排查 */
    private static final String THREAD_NAME_PREFIX = "log-async-";

    /** 线程空闲存活时间（秒） */
    private static final int KEEP_ALIVE_SECONDS = 60;

    /** 线程池关闭时的最大等待时间（秒） */
    private static final int AWAIT_TERMINATION_SECONDS = 60;

    /**
     * 配置日志异步线程池
     * <p>
     * 拒绝策略采用 CallerRunsPolicy：当线程池与队列都满时，由提交任务的线程
     * 自行执行任务，保证日志不丢失的同时对上游形成自然的背压。
     * </p>
     *
     * @return 线程池执行器
     */
    @Bean(name = "logAsyncExecutor")
    public Executor logAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 线程池与队列都满时，由调用线程执行任务，避免日志丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 应用关闭时等待剩余日志任务执行完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        executor.initialize();
        return executor;
    }
}
