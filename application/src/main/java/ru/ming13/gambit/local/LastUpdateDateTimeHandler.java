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

package ru.ming13.gambit.local;


import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import ru.ming13.gambit.remote.InternetDateTime;


class LastUpdateDateTimeHandler
{
	private final SQLiteDatabase database;

	public LastUpdateDateTimeHandler() {
		this.database = DbProvider.getInstance().getDatabase();
	}

	public void setCurrentDateTimeAsLastUpdated() {
		database.beginTransaction();
		try {
			trySetCurrentDateTimeAsLastUpdated();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetCurrentDateTimeAsLastUpdated() {
		if (!recordExists()) {
			insertEmptyRecord();
		}

		updateRecord(new InternetDateTime());
	}

	private boolean recordExists() {
		String recordsCountSelectionQuery = buildRecordsCountSelectingQuery();

		return DatabaseUtils.longForQuery(database, recordsCountSelectionQuery, null) > 0;
	}

	private String buildRecordsCountSelectingQuery() {
		return String.format("select count(*) from %s", DbTableNames.DB_LAST_UPDATE_TIME);
	}

	private void insertEmptyRecord() {
		database.execSQL(buildEmptyRecordInsertionQuery());
	}

	private String buildEmptyRecordInsertionQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("insert into %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		queryBuilder.append(String.format("(%s) ", DbFieldNames.DB_LAST_UPDATE_TIME));
		queryBuilder.append(String.format("values (%s)", "''"));

		return queryBuilder.toString();
	}

	private void updateRecord(InternetDateTime dateTime) {
		database.execSQL(buildRecordUpdatingQuery(dateTime));
	}

	private String buildRecordUpdatingQuery(InternetDateTime dateTime) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("update %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		queryBuilder.append(
			String.format("set %s='%s' ", DbFieldNames.DB_LAST_UPDATE_TIME, dateTime.toString()));

		return queryBuilder.toString();
	}

	public InternetDateTime getLastUpdatedDateTime() {
		database.beginTransaction();
		try {
			InternetDateTime lastUpdatedDateTime = tryGetLastUpdatedDateTime();
			database.setTransactionSuccessful();
			return lastUpdatedDateTime;
		}
		finally {
			database.endTransaction();
		}
	}

	private InternetDateTime tryGetLastUpdatedDateTime() {
		ensureRecordExists();

		Cursor databaseCursor = database.rawQuery(buildRecordSelectingQuery(), null);
		databaseCursor.moveToFirst();

		String dateTimeAsString = databaseCursor.getString(0);

		return new InternetDateTime(dateTimeAsString);
	}

	private void ensureRecordExists() {
		if (!recordExists()) {
			setCurrentDateTimeAsLastUpdated();
		}
	}

	private String buildRecordSelectingQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("select %s ", DbFieldNames.DB_LAST_UPDATE_TIME));
		queryBuilder.append(String.format("from %s", DbTableNames.DB_LAST_UPDATE_TIME));

		return queryBuilder.toString();
	}
}
