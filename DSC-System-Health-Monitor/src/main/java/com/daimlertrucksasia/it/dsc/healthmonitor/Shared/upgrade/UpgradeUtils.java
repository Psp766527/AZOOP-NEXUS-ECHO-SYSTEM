package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;// Converted version of UpgradeUtils.java


import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.mapping.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UpgradeUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpgradeUtils.class);

  public static Map<String, String> quotedNameMap(Dialect dialect, Table table) {
    Map<String, String> ret = new LinkedHashMap<>();
    for (Column col : table.getColumns()) {
      ret.put(col.getName(), col.getQuotedName(dialect));
    }
    return ret;
  }

  public static void createQuartzTables(ApplicationContext context, UpgradeScratchPad scratch) {
    try (InputStream is = context.getResource("classpath:db/quartz_tables.sql").getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
        if (line.trim().endsWith(";")) {
          String sql = sb.toString().trim();
          if (!sql.isEmpty()) {
            scratch.executeNoArgNativeUpdate(sql.substring(0, sql.length() - 1));
          }
          sb.setLength(0);
        }
      }

    } catch (IOException ex) {
      throw new PersistenceException("Failed to load and execute Quartz tables SQL", ex);
    }
  }

  public static String getAddTime(Dialect dialect, String column, int num, TimeUnit unit) {
    if (dialect instanceof H2Dialect) {
      return getAddTimeH2(column, num, unit);
    } else if (dialect instanceof PostgreSQLDialect) {
      return getAddTimePostgreSQL(column, num, unit);
    } else {
      throw new PersistenceException("Unsupported dialect " + dialect.getClass().getName());
    }
  }

  private static String getAddTimeH2(String column, int num, TimeUnit unit) {
    String unitName = unit.name();
    unitName = unitName.substring(0, unitName.length() - 1);
    return "TIMESTAMPADD(" + unitName + ", " + num + ", " + column + ")";
  }

  private static String getAddTimePostgreSQL(String column, int num, TimeUnit unit) {
    String addMinus = (num >= 0) ? "+" : "-";
    return "(" + column + " " + addMinus + " interval '" + Math.abs(num) + " " + unit.name().toLowerCase() + "')";
  }

  public static String getInsertAllColumns(Dialect dialect, Table table) {
    Map<String, String> cols = quotedNameMap(dialect, table);
    return getInsertColumns(dialect, table, cols);
  }

  public static String getInsertColumns(Dialect dialect, Table table, Map<String, String> quotedNameMap) {
    return getInsertColumnsNoValues(dialect, table, quotedNameMap) + " VALUES (" +
            quotedNameMap.keySet().stream().map(s -> ":" + s).collect(Collectors.joining(", ")) + ")";
  }

  public static String getInsertColumnsNoValues(Dialect dialect, Table table, Map<String, String> quotedCols) {
    return "INSERT INTO " + table.getQuotedName(dialect) + " (" +
            quotedCols.values().stream().collect(Collectors.joining(", ")) + ")";
  }

  public static String getSelectAllColumns(Dialect dialect, Table table) {
    Map<String, String> cols = quotedNameMap(dialect, table);
    return getSelectColumns(dialect, table, cols);
  }

  public static String getSelectColumns(Dialect dialect, Table table, Map<String, String> quotedNameMap) {
    return "SELECT " + quotedNameMap.entrySet().stream().map(e -> e.getValue() + " AS m" + e.getKey())
            .collect(Collectors.joining(", ")) + " FROM " + table.getQuotedName(dialect);
  }

  public static String quotedNames(Dialect dialect, String delimiter, Column... columns) {
    return Arrays.stream(columns).map(col -> col.getQuotedName(dialect)).collect(Collectors.joining(delimiter));
  }

  public static void throwErrorIf(boolean doThrow, String template, Object... parameters) {
    if (!doThrow) return;
    String message = MessageFormat.format(template, parameters);
    LOGGER.error(message);
    throw new PersistenceException(message);
  }

  public static Map<String, Object> toMap(Tuple tuple, Map<String, String> aliasMapping) {
    Map<String, Object> values = new LinkedHashMap<>();
    for (TupleElement<?> elem : tuple.getElements()) {
      String alias = elem.getAlias();
      String colName = aliasMapping.get(alias);
      if (colName == null) {
        String subAlias = alias.substring(0, 1).equalsIgnoreCase("m") ? alias.substring(1) : alias;
        colName = aliasMapping.getOrDefault(subAlias, alias);
      }
      values.put(colName, tuple.get(elem));
    }
    return values;
  }

  protected UpgradeUtils() {}

  public static void removeIndexByName(Table table, String name) {
    Map<String, Index> indexMap = table.getIndexes(); // Hibernate 6.x API
    if (indexMap != null) {
      indexMap.entrySet().removeIf(entry -> name.equalsIgnoreCase(entry.getKey()));
    }
  }


}
