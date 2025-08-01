package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.boot;


import org.springframework.context.ApplicationEvent;

@FunctionalInterface
public interface BootListener {
    void onEvent(ApplicationEvent event) throws Exception;
}

