package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;// Converted version of UpgradeUtils.java


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Table;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Migration (Upgrade process) for a database
 */
public abstract class DatabaseUpgrader implements DatabaseProcessor {

  public static final String CONFIG_DISALLOW = "db.upgrade.disallow";
  public static final String CONFIG_STOP_AT_VERSION = "db.upgrade.stopAtVersion";

  private static final Logger logger = LoggerFactory.getLogger(DatabaseUpgrader.class);

  protected final ApplicationContext applicationContext;

  public DatabaseUpgrader(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  private void createSchema(UpgradeScratchPad scratch) {
    logger.info("Creating fresh empty database...");
    scratch.startTransaction();

    for (Table table : scratch.metadata.collectTableMappings()) {
      scratch.createTable(table, true, true, true, false);
    }

    scratch.metadata.collectTableMappings().forEach(scratch::createTableForeignKeys);

    UpgradeUtils.createQuartzTables(applicationContext, scratch);
    createSchemaExtra(scratch);

    scratch.setDBVersion(scratch.latestVersion);
    scratch.endTransaction();
  }

  protected void createSchemaExtra(UpgradeScratchPad scratch) {}

  private void migrate(Metadata metadata, EntityManagerFactory factory) {
    EntityManager manager = factory.createEntityManager();
    try (UpgradeScratchPad scratch = newScratchPad(manager, metadata)) {
      logger.info("Starting migration process...");
      migrate(scratch);
    } finally {
      logger.info("Migration process complete.");
    }
  }

  private void migrate(UpgradeScratchPad scratch) {
    int stopAtVersion = scratch.latestVersion;
    List<String> tables = scratch.getTables();

    if (tables.isEmpty()) {
      createSchema(scratch);
      return;
    } else {
      tables.replaceAll(String::toLowerCase);
    }

    int version = 0;
    if (tables.contains("version")) {
      version = scratch.getDBVersion();
    }

    logger.info("Database is currently version {}", version);
    if (version != stopAtVersion) {
      logger.info("Migrating to version {}", stopAtVersion);
    }

    LinkedList<Upgrader> upgraders = new LinkedList<>();
    for (int upgradeTo = stopAtVersion; upgradeTo > version;) {
      Upgrader upgrader = applicationContext.getBean(Upgrader.class, new ToVersion.Impl(upgradeTo));
      upgraders.addFirst(upgrader);
      upgradeTo = upgrader.getClass().getAnnotation(FromVersion.class).value();
    }

    for (Upgrader upgrader : upgraders) {
      int upgradeFrom = upgrader.getClass().getAnnotation(FromVersion.class).value();
      int upgradeTo = upgrader.getClass().getAnnotation(ToVersion.class).value();
      logger.info("Upgrading from {} to {}", upgradeFrom, upgradeTo);
      scratch.startTransaction();
      upgrader.upgrade(scratch);
      scratch.setDBVersion(upgradeTo);
      scratch.endTransaction();
    }
  }

  protected abstract UpgradeScratchPad newScratchPad(EntityManager manager, Metadata metadata);

  @Override
  public void postProcessDatabase(Metadata metadata, SessionFactoryImplementor sessionFactory) {
    boolean disallowUpgrade = Boolean.parseBoolean(
            applicationContext.getEnvironment().getProperty(CONFIG_DISALLOW, "false")
    );

    if (disallowUpgrade) {
      logger.debug("Skipping database migration");
      return;
    }

    try {
      migrate(metadata, sessionFactory);
    } catch (PersistenceException ex) {
      logger.error("Failed to perform migration", ex);
    }
  }

  @Override
  public void preProcessDatabase(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {}
}
