/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
