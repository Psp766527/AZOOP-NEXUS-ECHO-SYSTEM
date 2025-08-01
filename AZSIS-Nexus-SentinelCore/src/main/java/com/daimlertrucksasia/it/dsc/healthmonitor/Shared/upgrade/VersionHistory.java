package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import java.util.Map;
import java.util.TreeMap;

/**
 * Manages modifiable snapshots of tables that can be altered as necessary
 * to represent the Database at various schema versions.
 */
public final class VersionHistory {

  /** Cache of modifiable tables based on entity name/role */
  private final Map<String, Table> tables = new TreeMap<>();

  /** UpgradeScratchPad instance */
  public final UpgradeScratchPad scratch;

  /** Current version of the database schema */
  public int dbVersion;

  public VersionHistory(UpgradeScratchPad scratch) {
    this.scratch = scratch;
    this.dbVersion = scratch.latestVersion;
  }

  public Table getTable(Class<?> entityType) {
    return getTable(entityType.getName());
  }

  public Table getTable(Class<?> entityType, String role) {
    return getTable(entityType.getName(), role);
  }

  public Table getTable(String entityName) {
    return tables.computeIfAbsent(entityName, name -> {
      PersistentClass pClass = scratch.metadata.getEntityBinding(name);
      if (pClass != null) {
        return (Table) scratch.dialect.getModelEditor().clone(pClass.getTable());
      }
      return null;
    });
  }

  public Table getTable(String entityName, String role) {
    String bind = entityName + "." + role;
    return tables.computeIfAbsent(bind, name -> {
      Collection col = scratch.metadata.getCollectionBinding(name);
      if (col != null) {
        return (Table) scratch.dialect.getModelEditor().clone(col.getCollectionTable());
      }
      return null;
    });
  }


  public void putTable(Class<?> entityType, Table table) {
    putTable(entityType.getName(), table);
  }

  public void putTable(Class<?> entityType, String role, Table table) {
    putTable(entityType.getName(), role, table);
  }

  public void putTable(String entityName, Table table) {
    tables.put(entityName, table);
  }

  public void putTable(String entityName, String role, Table table) {
    String bind = entityName + "." + role;
    tables.put(bind, table);
  }
}
// --- Additional Interface and Implementation ---




class DefaultModelEditor implements ModelEditor {
  @Override
  public Table clone(Table original) {
    Table cloned = new Table(original.getName());

    for (Column col : original.getColumns()) {
      Column colClone = new Column(col.getName());
      colClone.setNullable(col.isNullable());
      colClone.setSqlType(col.getSqlType());
      colClone.setLength(col.getLength());
      colClone.setPrecision(col.getPrecision());
      colClone.setScale(col.getScale());
      cloned.addColumn(colClone);
    }


    if (original.getPrimaryKey() != null) {
      cloned.setPrimaryKey(original.getPrimaryKey());
    }

    return cloned;
  }
}


