package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

import jakarta.persistence.*;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.spi.MetadataBuildingContext;

import org.hibernate.mapping.*;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class UpgradeScratchPad implements AutoCloseable {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected final EntityManager entityManager;
  protected final Metadata metadata;
  protected final int latestVersion;
  protected final DialectExt dialect; // You must define this interface/class

  protected UpgradeScratchPad(EntityManager entityManager, Metadata metadata, int latestVersion, DialectExt dialect) {
    this.entityManager = Objects.requireNonNull(entityManager, "EntityManager is required");
    this.metadata = Objects.requireNonNull(metadata, "Metadata is required");
    this.dialect = Objects.requireNonNull(dialect, "Dialect is required");
    this.latestVersion = latestVersion;
  }

  public abstract int getDBVersion();

  public abstract void setDBVersion(int version);

  public void startTransaction() {
    EntityTransaction tx = entityManager.getTransaction();
    if (!tx.isActive()) tx.begin();
  }

  public void endTransaction() {
    EntityTransaction tx = entityManager.getTransaction();
    if (tx.isActive()) tx.commit();
  }

  @Override
  public void close() {
    if (entityManager.isOpen()) {
      entityManager.close();
    }
  }

  // ========== DDL ==========

  public void createTable(Table table, boolean schema, boolean uniqueKeys, boolean indexes, boolean foreignKeys) {
    executeNoArgNativeUpdate(dialect.getCreateTableSql(metadata, table, schema, uniqueKeys, indexes, foreignKeys));
  }

  public void createTableSchema(Table table) {
    executeNoArgNativeUpdate(dialect.getCreateTableSchemaSql(metadata, table));
  }

  public void createTableIndexes(Table table) {
    executeNoArgNativeUpdate(dialect.getCreateTableIndexesSql(metadata, table));
  }

  public void createTableUniqueKeys(Table table) {
    List<String> sqls = dialect.getCreateTableUniqueKeysSql(metadata, table);
    executeNoArgNativeUpdate(sqls);
  }

  public void createTableForeignKeys(Table table) {
    executeNoArgNativeUpdate(dialect.getCreateTableForeignKeysSql(metadata, table));
  }

  public void createColumn(Table table, Column column) {
    executeNoArgNativeUpdate(dialect.getAddColumnSql(metadata, table, column));
  }

  public void alterColumn(Table table, Column column) {
    executeNoArgNativeUpdate(dialect.getAlterColumnSql(metadata, table, column));
  }

  public void dropTable(Table table) {
    executeNoArgNativeUpdate(dialect.getDropTableSql(metadata, table));
  }

  public void dropTables(Table... tables) {
    Arrays.stream(tables).forEach(this::dropTable);
  }

  public void dropColumn(Table table, String columnName) {
    executeNoArgNativeUpdate(dialect.getDropColumnSQL(table, columnName));
  }

  public void createIndex(Index index) {
    executeNoArgNativeUpdate(dialect.getCreateIndexSql(metadata, index));
  }

  public void createUniqueKey(UniqueKey key) {
    executeNoArgNativeUpdate(dialect.getCreateUniqueKeySql(metadata, key));
  }

  public void createForeignKey(ForeignKey key) {
    executeNoArgNativeUpdate(dialect.getCreateForeignKeySql(metadata, key));
  }

  public void dropForeignKey(ForeignKey key) {
    executeNoArgNativeUpdate(dialect.getDropForeignKeySql(metadata, key));
  }

  public void dropUniqueKey(UniqueKey key) {
    executeNoArgNativeUpdate(dialect.getDropUniqueKeySql(metadata, key));
  }

  public void renameColumn(Table table, String newName, String oldName) {
    Column column = table.getColumn(Identifier.toIdentifier(newName));
    executeNoArgNativeUpdate(dialect.getRenameColumnSql(metadata, table, oldName, column));
  }

  public void renameIndex(Table table, String newName, String oldName) {
    Index index = table.getIndex(newName);
    executeNoArgNativeUpdate(dialect.getRenameIndexSql(metadata, oldName, index));
  }

  public void renameUniqueKey(Table table, String newName, String oldName) {
    UniqueKey key = table.getUniqueKey(newName);
    executeNoArgNativeUpdate(dialect.getRenameUniqueKeySql(metadata, oldName, key));
  }

  public void renameTable(Table table, String oldName) {
    executeNoArgNativeUpdate(dialect.getRenameTableSql(metadata, oldName, table));
  }

  // ========== SQL Execution ==========

  public int executeNoArgNativeUpdate(String sql) {
    int count = entityManager.createNativeQuery(sql).executeUpdate();
    logger.info("Executed SQL: {}, Rows affected: {}", sql, count);
    return count;
  }


  public void executeNoArgNativeUpdate(List<String> sqlList) {
    sqlList.forEach(this::executeNoArgNativeUpdate);
  }

  public void executeNoArgNativeUpdate(String[] sqlArr) {
    Arrays.stream(sqlArr).forEach(this::executeNoArgNativeUpdate);
  }

  @SuppressWarnings("unchecked")
  public List<String> getTables() {
    Query query = entityManager.createNativeQuery(dialect.getListTablesSql(), Tuple.class);
    Stream<Tuple> result = (Stream<Tuple>) query.getResultStream();
    return result.map(t -> t.get(0, String.class))
            .collect(Collectors.toList());
  }



  public TypedQuery<Tuple> createNativeQuery(String sql) {
    return entityManager.createQuery(sql, Tuple.class);
  }

  public BasicValue createIdentityValue(Table table, Class<?> idType, MetadataBuildingContext context) {
    BasicValue value = new BasicValue(context, table);
    value.setTypeName(idType.getName());
    value.setIdentifierGeneratorStrategy("identity");
    return value;
  }

}
