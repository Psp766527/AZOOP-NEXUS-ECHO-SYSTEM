package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;


import org.quartz.Scheduler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Starts Quartz after Spring Boot is fully up. */
@Component
public class QuartzStarter implements ApplicationRunner {

    private final Scheduler scheduler;
    public QuartzStarter(Scheduler scheduler) { this.scheduler = scheduler; }

    @Override public void run(ApplicationArguments args) throws Exception {
        if (!scheduler.isStarted()) scheduler.start();
    }
}


