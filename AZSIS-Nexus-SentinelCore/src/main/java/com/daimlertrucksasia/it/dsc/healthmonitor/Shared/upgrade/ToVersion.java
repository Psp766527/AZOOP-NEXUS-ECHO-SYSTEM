package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;



import jakarta.inject.Qualifier;

import java.lang.annotation.*;

/**
 * DB version that the {@link Upgrader} upgrades to.
 */
@Qualifier
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToVersion {
  int value();

  /**
   * Runtime implementation of the ToVersion annotation (used for dynamic injection).
   */
  class Impl implements ToVersion {

    private final int version;

    public Impl(int version) {
      this.version = version;
    }

    @Override
    public int value() {
      return version;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return ToVersion.class;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ToVersion)) return false;
      return ((ToVersion) obj).value() == this.version;
    }

    @Override
    public int hashCode() {
      return (127 * "value".hashCode()) ^ Integer.hashCode(version);
    }

    @Override
    public String toString() {
      return "@" + ToVersion.class.getName() + "(value=" + version + ")";
    }
  }
}
