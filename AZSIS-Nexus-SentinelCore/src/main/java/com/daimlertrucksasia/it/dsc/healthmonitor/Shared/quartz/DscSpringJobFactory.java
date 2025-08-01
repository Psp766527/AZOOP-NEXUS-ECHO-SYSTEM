package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;


import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/** Creates & autowires job beans, merges JobDataMaps, copies props to setters. */
@Component
public final class DscSpringJobFactory extends SpringBeanJobFactory
        implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext ctx) {
        this.beanFactory = ctx.getAutowireCapableBeanFactory();
    }

    @Override
    public @NotNull Job newJob(@NotNull TriggerFiredBundle bundle,
                               @NotNull Scheduler scheduler) throws SchedulerException {

        Job job = beanFactory.createBean(bundle.getJobDetail().getJobClass());

        JobDataMap data = new JobDataMap();
        data.putAll(scheduler.getContext());
        data.putAll(bundle.getJobDetail().getJobDataMap());
        data.putAll(bundle.getTrigger().getJobDataMap());

        BeanWrapper bw = new BeanWrapperImpl(job);
        data.forEach((k, v) -> {
            if (bw.isWritableProperty(k)) {
                try { bw.setPropertyValue(k, v); } catch (Exception ignored) { }
            }
        });
        return job;
    }
}
