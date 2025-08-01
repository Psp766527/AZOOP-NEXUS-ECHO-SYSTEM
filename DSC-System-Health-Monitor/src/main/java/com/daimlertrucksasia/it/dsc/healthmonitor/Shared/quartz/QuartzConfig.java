package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;



import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import org.quartz.spi.SchedulerPlugin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Central Quartz factory – no direct plugin setter call (handled by customizer).
 */
@Configuration
@EnableConfigurationProperties(DscQuartzProps.class)
public class QuartzConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            DscSpringJobFactory jobFactory,
            QuartzProperties quartzProps,
            ApplicationContext ctx,
            List<SchedulerPlugin> plugins   // forces Spring to collect plugin beans
    ) {
        Properties extra = new Properties();
        extra.putAll(quartzProps.getProperties());

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setApplicationContext(ctx);
        factory.setApplicationContextSchedulerContextKey("springContext");
        factory.setOverwriteExistingJobs(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setQuartzProperties(extra);
        return factory;     // produces single Scheduler bean
    }

    /** Optional: bootstrap Quartz tables from SQL file. */
    @Bean
    @ConditionalOnProperty(name = "dsc.quartz.load-tables-sql-file")
    public CommandLineRunner createQuartzTables(
            DataSource dataSource, ResourceLoader loader, DscQuartzProps props) {

        return args -> {
            String sql = QuartzSupport.readSql(loader, props.getLoadTablesSqlFile());
            try (Connection c = dataSource.getConnection();
                 Statement  s = c.createStatement()) {
                for (String stmt : sql.split(";")) {
                    if (!stmt.isBlank()) s.execute(stmt);
                }
            }
        };
    }
}
