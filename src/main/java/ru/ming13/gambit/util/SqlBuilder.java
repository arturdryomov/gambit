package ru.ming13.gambit.util;

import android.text.TextUtils;

public class SqlBuilder
{
	private SqlBuilder() {
	}

	public static String buildCopyTableClause(String sourceTable, String destinationTable) {
		return String.format("insert into %s select * from %s", destinationTable, sourceTable);
	}

	public static String buildCreateTableClause(String table, String description) {
		return String.format("create table %s (%s)", table, description);
	}

	public static String buildCreateTempTableClause(String table, String description) {
		return String.format("create temporary table %s (%s)", table, description);
	}

	public static String buildDropTableClause(String table) {
		return String.format("drop table %s", table);
	}

	public static String buildPragmaForeignKeysClause() {
		return "pragma foreign_keys = on";
	}

	public static String buildSelectionClause(String field, long id) {
		return String.format("%s = %d", field, id);
	}

	public static String buildTableDescription(String... columnDescriptions) {
		return TextUtils.join(",", columnDescriptions);
	}

	public static String buildTableColumnDescription(String columnName, String columnParameters) {
		return String.format("%s %s", columnName, columnParameters);
	}
}
