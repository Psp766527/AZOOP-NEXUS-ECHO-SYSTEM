package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;




import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;

public class DSCQuartzPlugin implements SchedulerPlugin {

    private String someProperty;       // will be injected via YAML

    // Setter name must match YAML key: someProperty → setSomeProperty
    public void setSomeProperty(String someProperty) {
        this.someProperty = someProperty;
    }

//    @Override
//    public void initialize(String pluginName,
//                           Scheduler scheduler,
//                           ClassLoadHelper helper) throws SchedulerException {
//
//        System.out.printf("[DSCQuartzPlugin] initialize: name=%s, prop=%s%n",
//                pluginName, someProperty);
//        // plugin setup logic here
//    }

    @Override
    public void initialize(String pluginName,
                           Scheduler scheduler,
                           ClassLoadHelper helper) throws SchedulerException {

        /* 1️⃣  Publish the flag in SchedulerContext so any job can read it */
        scheduler.getContext().put("stopLoadCacheJob", someProperty);

        /* 2️⃣  If flag is true → disable all “load‑cache” triggers */
        if (Boolean.parseBoolean(someProperty)) {
            //  Assumes your load‑cache job/trigger group is called "cache-loader".
            //  Adjust group or key names if your cron trigger uses different IDs.
            var matcher = GroupMatcher.triggerGroupEquals("cache-loader");
            for (TriggerKey tk : scheduler.getTriggerKeys(matcher)) {
                scheduler.pauseTrigger(tk);
                scheduler.unscheduleJob(tk);
                System.out.printf("[DSCQuartzPlugin] disabled cache trigger %s%n", tk);
            }
        }

        /* 3️⃣  Register a simple JobListener for runtime diagnostics */
        scheduler.getListenerManager().addJobListener(new JobListenerSupport() {
            @Override public String getName() { return "DSCPluginJobLogger"; }

            @Override
            public void jobWasExecuted(JobExecutionContext ctx,
                                       JobExecutionException ex) {
                System.out.printf("[DSCQuartzPlugin] job %s ran in %d ms %s%n",
                        ctx.getJobDetail().getKey(),
                        ctx.getJobRunTime(),
                        ex == null ? "✔" : ("✖ " + ex.getMessage()));
            }
        });

        System.out.printf("[DSCQuartzPlugin] initialize complete – flag=%s%n", someProperty);
    }


    @Override public void start()    { System.out.println("[DSCQuartzPlugin] start"); }

    @Override public void shutdown() { System.out.println("[DSCQuartzPlugin] shutdown"); }
}
