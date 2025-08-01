package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the source database version that an {@link Upgrader} implementation upgrades from.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromVersion {
  /**
   * The database version from which the upgrade starts.
   *
   * @return version number (e.g., 110)
   */
  int value();
}
