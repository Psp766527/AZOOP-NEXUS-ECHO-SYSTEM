package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;


import org.hibernate.boot.Metadata;
import org.hibernate.mapping.*;

import java.util.List;

/**
 * Custom wrapper for Hibernate Dialect to provide upgrade-related SQL utilities.
 */
public interface DialectExt {
    String getListTablesSql();

    String getDropColumnSQL(Table table, String columnName);

    List<String> getCreateTableUniqueKeysSql(Metadata metadata, Table table);

    String getCreateTableSchemaSql(Metadata metadata, Table table);

    String getCreateTableIndexesSql(Metadata metadata, Table table);

    List<String> getCreateTableSql(Metadata metadata, Table table, boolean schema, boolean uniqueKeys, boolean indexes, boolean foreignKeys);

    String getAddColumnSql(Metadata metadata, Table table, Column column);

    String getAlterColumnSql(Metadata metadata, Table table, Column column);

    String getDropTableSql(Metadata metadata, Table table);

    String getCreateIndexSql(Metadata metadata, Index index);

    String getCreateUniqueKeySql(Metadata metadata, UniqueKey key);

    String getCreateForeignKeySql(Metadata metadata, ForeignKey key);

    String getDropForeignKeySql(Metadata metadata, ForeignKey key);

    String getDropUniqueKeySql(Metadata metadata, UniqueKey key);

    String getRenameColumnSql(Metadata metadata, Table table, String oldName, Column column);

    String getRenameIndexSql(Metadata metadata, String oldName, Index index);

    String getRenameUniqueKeySql(Metadata metadata, String oldName, UniqueKey key);

    String getRenameTableSql(Metadata metadata, String oldName, Table table);

    String getCreateTableForeignKeysSql(Metadata metadata, Table table);


    ModelEditor getModelEditor();
}
