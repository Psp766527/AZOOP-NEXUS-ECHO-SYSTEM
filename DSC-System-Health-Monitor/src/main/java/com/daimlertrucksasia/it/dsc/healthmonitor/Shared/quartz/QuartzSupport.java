package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.quartz.utils.Key;
import org.springframework.core.io.ResourceLoader;

public final class QuartzSupport {

    /** Grouping for jobs/triggers that are not periodic but one‑shot. */
    public static final String GROUP_ONE_SHOT = "one-shot";

    /** Simple helper to replace old static method. */
    public static boolean isOneShot(Key<?> key) {
        return key != null && GROUP_ONE_SHOT.equals(key.getGroup());
    }

    /** Load entire SQL file into a String. */
    public static String readSql(ResourceLoader loader, String location) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(loader.getResource(location).getInputStream(),
                        StandardCharsets.UTF_8))) {
            return br.lines().reduce("", (a, b) -> a + '\n' + b);
        }
    }

    private QuartzSupport() {}
}
