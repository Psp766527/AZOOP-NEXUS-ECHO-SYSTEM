package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

/**
 * Performs a version upgrade, generally from one version to another.
 * Skipping versions in upgrades is technically supported, though not currently utilized.
 */
public interface Upgrader {

  /**
   * Downgrade the schema back to the version prior to this upgrade
   * @param history Versioned history of the database to downgrade
   */
  void downgradeSchema(VersionHistory history);

  /**
   * Perform an upgrade against the database
   * @param scratch UpgradeScratchPad to perform the upgrade with
   */
  void upgrade(UpgradeScratchPad scratch);
}
