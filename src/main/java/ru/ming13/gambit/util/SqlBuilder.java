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

	public static String buildAttachingClause(String file, String alias) {
		return String.format("attach '%s' as %s", file, alias);
	}

	public static String buildDetachingClause(String alias) {
		return String.format("detach %s", alias);
	}

	public static String buildDeletionClause(String table) {
		return String.format("delete from %s", table);
	}

	public static String buildInsertionClause(String table, String alias) {
		return String.format("insert into %s select * from %s.%s", table, alias, table);
	}

	public static String buildSelectionClause(String field, long id) {
		return String.format("%s = %d", field, id);
	}

	public static String buildTableCreationClause(String table, String description) {
		return String.format("create table %s (%s)", table, description);
	}

	public static String buildTableDescription(String... columnDescriptions) {
		return TextUtils.join(",", columnDescriptions);
	}

	public static String buildTableColumnDescription(String columnName, String columnParameters) {
		return String.format("%s %s", columnName, columnParameters);
	}
}
