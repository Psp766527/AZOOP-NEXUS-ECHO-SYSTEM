package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.concurrent;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Provides a configurable background task scheduler for Spring components
 * that rely on {@link org.springframework.scheduling.TaskScheduler}.
 *
 * <p>Replaces the old Jersey {@code BGThreadPoolProvider}.</p>
 */
@Slf4j
@Configuration
public class BgSchedulerConfig {

    /** Property key mirrors the constant in the old Jersey class. */
    public static final String PROP_POOL_SIZE =
            "dsc.background-scheduler.pool-size";

    /**
     * Creates a {@link ThreadPoolTaskScheduler} with the configured pool size.
     *
     * @param poolSize value from {@code application.yaml}; defaults to 1
     */
    @Bean
    public ThreadPoolTaskScheduler backgroundTaskScheduler(
            @Value("${" + PROP_POOL_SIZE + ":1}") int poolSize) {

        log.info("[BG‑Scheduler] Initialising with pool size={}", poolSize);

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix("dsc-bg-");
        scheduler.initialize();
        return scheduler;
    }
}

