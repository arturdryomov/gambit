package app.android.simpleflashcards.models;


import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.android.simpleflashcards.InternetDateTimeFormatter;


class LastUpdateTimeHandler
{
	private final SQLiteDatabase database;

	LastUpdateTimeHandler() {
		this.database = DatabaseProvider.getInstance().getDatabase();
	}

	void setCurrentTimeAsLastUpdated() {
		database.beginTransaction();
		try {
			trySetCurrentTimeAsLastUpdated();
			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void trySetCurrentTimeAsLastUpdated() {
		if (!recordExists()) {
			insertEmptyRecord();
		}

		updateRecord(new Date());
	}

	private void insertEmptyRecord() {
		database.execSQL(buildEmptyRecordInsertionQuery());
	}

	private String buildEmptyRecordInsertionQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("insert into %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("(%s) ", DbFieldNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("values (%s)", new String()));

		return builder.toString();
	}

	private void updateRecord(Date time) {
		String timeAsString = InternetDateTimeFormatter.convertToString(time);
		database.execSQL(buildRecordUpdatingQuery(timeAsString));
	}

	private String buildRecordUpdatingQuery(String timeAsString) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("set %s=%s ", DbFieldNames.DB_LAST_UPDATE_TIME, timeAsString));

		return builder.toString();
	}

	Date getLastUpdatedTime() {
		database.beginTransaction();
		try {
			Date lastUpdatedTime = tryGetLastUpdatedTime();
			database.setTransactionSuccessful();
			return lastUpdatedTime;
		}
		finally {
			database.endTransaction();
		}
	}

	private Date tryGetLastUpdatedTime() {
		ensureRecordExists();

		Cursor cursor = database.rawQuery(buildRecordSelectingQuery(), null);
		cursor.moveToFirst();

		String timeAsString = cursor.getString(0);

		return InternetDateTimeFormatter.parse(timeAsString);
	}

	private void ensureRecordExists() {
		if (!recordExists()) {
			setCurrentTimeAsLastUpdated();
		}
	}

	private String buildRecordSelectingQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select %s ", DbFieldNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("from %s", DbTableNames.DB_LAST_UPDATE_TIME));

		return builder.toString();
	}

	private boolean recordExists() {
		Cursor cursor = database.rawQuery(buildRecordsCountSelectingQuery(), null);
		cursor.moveToFirst();

		int recordsCount = cursor.getInt(0);

		return recordsCount > 0;
	}

	private String buildRecordsCountSelectingQuery() {
		return String.format("select count(*) from %s", DbTableNames.DB_LAST_UPDATE_TIME);
	}
}
