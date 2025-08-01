package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;

/**
 * Small bean to hold names of one or more indexes on a Table
 */
public class IndexMeta {

  public static void create(VersionHistory history, List<IndexMeta> indexes) {
    for (IndexMeta idxMeta : indexes) {
      idxMeta.create(history);
    }
  }

  public static void remove(VersionHistory history, List<IndexMeta> indexes) {
    for (IndexMeta idxMeta : indexes) {
      idxMeta.remove(history);
    }
  }

  public final String entityName;
  public final String roleName;
  public final List<String> indexNames;

  public IndexMeta(String entityName, String roleName, String... indexNames) {
    this.entityName = entityName;
    this.roleName = roleName;
    this.indexNames = Arrays.asList(indexNames);
  }

  /**
   * Create the indexes in the database
   * @param history history to create indexes within
   */
  public void create(VersionHistory history) {
    for (Index index : getIndexes(history)) {
      history.scratch.createIndex(index);
    }
  }

  public Table getTable(VersionHistory history) {
    return (roleName == null) ? history.getTable(entityName) : history.getTable(entityName, roleName);
  }

  public List<Index> getIndexes(VersionHistory history) {
    Table table = getTable(history);
    return indexNames.stream().map(indexName -> table.getIndex(indexName)).collect(Collectors.toList());
  }

  /**
   * Remove the indexes from the modeling
   * @param history {@link VersionHistory} to remove the indexes from
   */
  public void remove(VersionHistory history) {
    Table table = getTable(history);
    for (String name : indexNames) {
      UpgradeUtils.removeIndexByName(table, name);
    }
  }
}
