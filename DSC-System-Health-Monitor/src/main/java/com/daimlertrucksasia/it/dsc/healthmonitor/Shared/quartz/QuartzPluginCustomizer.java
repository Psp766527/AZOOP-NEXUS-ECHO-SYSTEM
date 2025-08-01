package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;


import java.lang.reflect.Method;
import java.util.List;
import org.quartz.spi.SchedulerPlugin;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Registers every SchedulerPlugin bean, whatever setter name the runtime
 * SchedulerFactoryBean happens to expose (setSchedulerPlugins or setPlugins).
 */
@Configuration
public class QuartzPluginCustomizer {

    @Bean
    SchedulerFactoryBeanCustomizer pluginCustomizer(List<SchedulerPlugin> plugins) {
        return factory -> inject(factory, plugins.toArray(new SchedulerPlugin[0]));
    }

    private static void inject(SchedulerFactoryBean f, SchedulerPlugin[] arr) {
        if (!invoke(f, "setSchedulerPlugins", arr) && !invoke(f, "setPlugins", arr)) {
            System.err.println("[QuartzPluginCustomizer] no plugin setter on "
                    + f.getClass().getName() + " – check your dependencies.");
        }
    }
    private static boolean invoke(SchedulerFactoryBean f, String m, SchedulerPlugin[] a) {
        try {
            Method mm = f.getClass().getMethod(m, SchedulerPlugin[].class);
            mm.invoke(f, (Object) a);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to call " + m, t);
        }
    }
}

