package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.boot;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
@Component
public class BootTrigger implements ApplicationListener<ApplicationEvent> {

    private final Supplier<List<BootListener>> listeners;
    private final AtomicReference<List<BootListener>> cache = new AtomicReference<>();

    public BootTrigger(ObjectProvider<List<BootListener>> provider) {
        this.listeners = () -> cache.updateAndGet(
                existing -> existing != null ? existing
                        : provider.getIfAvailable(List::of));
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationEvent event) {
        for (BootListener listener : listeners.get()) {
            try {
                listener.onEvent(event);
            } catch (Exception ex) {
                log.error("BootListener {} failed to process {}",
                        listener.getClass().getSimpleName(),
                        event.getClass().getSimpleName(), ex);
            }
        }
    }
}

