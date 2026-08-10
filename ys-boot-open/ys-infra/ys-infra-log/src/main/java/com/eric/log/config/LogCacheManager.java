package com.eric.log.config;

import com.eric.log.module.dto.SysLoggerDTO;
import com.eric.log.module.service.SysLoggerService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 日志缓存管理器
 * <p>
 * 采用「内存队列 + 批量阈值触发 + 定时刷新」的方式缓存日志数据，
 * 避免每条日志都单独写库带来的性能损耗。写库失败时日志会重新入队等待重试。
 * </p>
 *
 * @ClassName:  LogCacheManager
 * @author:     Eric
 * @date:       2025-01-17
 * @Copyright   ERIC 微信公众号：忆笙的技术杂货库
 */
@Component
@Slf4j
public class LogCacheManager {

    /** 日志服务，用于批量落库 */
    @Autowired
    private SysLoggerService loggerService;

    /** 批量写入阈值：队列达到该数量时立即触发写库 */
    private static final int BATCH_SIZE = 100;

    /** 定时刷新间隔（毫秒） */
    private static final long FLUSH_INTERVAL_MILLIS = 5000L;

    /** 日志队列，使用并发队列保证线程安全 */
    private final ConcurrentLinkedQueue<SysLoggerDTO> logQueue = new ConcurrentLinkedQueue<>();

    /** 刷新锁，防止并发刷新导致重复写入 */
    private final ReentrantLock flushLock = new ReentrantLock();

    /**
     * 添加日志到缓存队列
     * <p>
     * 当队列长度达到批量阈值时自动触发一次批量写入。
     * </p>
     *
     * @param logger 日志数据传输对象
     */
    public void addLog(SysLoggerDTO logger) {
        if (logger != null) {
            logQueue.offer(logger);
            if (logQueue.size() >= BATCH_SIZE) {
                flush();
            }
        }
    }

    /**
     * 批量刷新日志到数据库
     * <p>
     * 使用 tryLock 避免并发刷新；若写库失败，日志会重新放回队列等待下次重试。
     * </p>
     */
    public void flush() {
        // 尝试获取锁，获取失败说明已有线程在刷新，直接返回
        if (flushLock.tryLock()) {
            try {
                if (logQueue.isEmpty()) {
                    return;
                }

                List<SysLoggerDTO> batch = new ArrayList<>(BATCH_SIZE);
                SysLoggerDTO loggerItem;
                // 每次最多取出 BATCH_SIZE 条日志
                while (batch.size() < BATCH_SIZE && (loggerItem = logQueue.poll()) != null) {
                    batch.add(loggerItem);
                }

                if (!batch.isEmpty()) {
                    try {
                        loggerService.batchAdd(batch);
                    } catch (Exception e) {
                        // 写入失败，将日志重新放回队列，避免日志丢失
                        log.error("批量写入日志失败，数量: {}, 错误: {}", batch.size(), e.getMessage(), e);
                        batch.forEach(logQueue::offer);
                    }
                }
            } finally {
                flushLock.unlock();
            }
        }
    }

    /**
     * 定时刷新任务
     * <p>
     * 保证即使未达到批量阈值，日志也能在固定间隔内及时落库。
     * </p>
     */
    @Scheduled(fixedDelay = FLUSH_INTERVAL_MILLIS, initialDelay = FLUSH_INTERVAL_MILLIS)
    public void scheduledFlush() {
        flush();
    }

    /**
     * 应用关闭前刷新剩余日志，避免服务停止时丢失队列中的数据
     */
    @PreDestroy
    public void destroy() {
        flush();
    }

    /**
     * 获取当前队列大小，用于监控日志积压情况
     *
     * @return 队列中待写入的日志数量
     */
    public int getQueueSize() {
        return logQueue.size();
    }

    /**
     * 清空日志队列
     * <p>
     * <b>注意：</b>该方法会丢弃队列中所有未写入的日志，仅用于测试或特殊场景。
     * </p>
     */
    public void clear() {
        flushLock.lock();
        try {
            logQueue.clear();
        } finally {
            flushLock.unlock();
        }
    }
}
