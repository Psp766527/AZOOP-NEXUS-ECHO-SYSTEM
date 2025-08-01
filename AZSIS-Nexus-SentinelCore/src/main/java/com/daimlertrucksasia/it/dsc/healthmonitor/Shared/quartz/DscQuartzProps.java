package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "dsc.quartz")
public class DscQuartzProps {

    /** Path (classpath: or file:) to the DDL that creates Quartz tables. */
    private String loadTablesSqlFile;

    /** Name of the JPA persistence unit should you still need it. */
    private String unitName;

    /** Replaces the old "cronjob.stopLoadCacheJob" flag. */
    private boolean stopLoadCacheJob;

    // getters & setters …
}

