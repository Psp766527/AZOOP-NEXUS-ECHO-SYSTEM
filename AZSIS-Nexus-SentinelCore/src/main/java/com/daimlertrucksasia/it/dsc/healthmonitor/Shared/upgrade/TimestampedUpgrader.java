package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * {@link Upgrader} that has a time stamp based on when it processes
 */
public abstract class TimestampedUpgrader implements Upgrader {

  /** time in which the upgrade started processing */
  protected OffsetDateTime now;

  @Override
  public final void upgrade(UpgradeScratchPad scratch) {
    now = OffsetDateTime.now(ZoneOffset.UTC);
    upgrade2(scratch);
  }

  protected abstract void upgrade2(UpgradeScratchPad scratch);
}
