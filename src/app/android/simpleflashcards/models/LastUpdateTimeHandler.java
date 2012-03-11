package app.android.simpleflashcards.models;


import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.android.simpleflashcards.InternetDateTimeFormatter;


class LastUpdateTimeHandler
{
	private final SQLiteDatabase database;

	public LastUpdateTimeHandler() {
		this.database = DatabaseProvider.getInstance().getDatabase();
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

		updateRecord(new Date());
	}

	private boolean recordExists() {
		Cursor cursor = database.rawQuery(buildRecordsCountSelectingQuery(), null);
		cursor.moveToFirst();

		int recordsCount = cursor.getInt(0);

		return recordsCount > 0;
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

	private void updateRecord(Date date) {
		String dateTimeAsString = InternetDateTimeFormatter.convertToString(date);
		database.execSQL(buildRecordUpdatingQuery(dateTimeAsString));
	}

	private String buildRecordUpdatingQuery(String dateTimeAsString) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("set %s=%s ", DbFieldNames.DB_LAST_UPDATE_TIME, dateTimeAsString));

		return builder.toString();
	}

	public Date getLastUpdatedDateTime() {
		database.beginTransaction();
		try {
			Date lastUpdatedDateTime = tryGetLastUpdatedDateTime();
			database.setTransactionSuccessful();
			return lastUpdatedDateTime;
		}
		finally {
			database.endTransaction();
		}
	}

	private Date tryGetLastUpdatedDateTime() {
		ensureRecordExists();

		Cursor cursor = database.rawQuery(buildRecordSelectingQuery(), null);
		cursor.moveToFirst();

		String dateTimeAsString = cursor.getString(0);

		return InternetDateTimeFormatter.parse(dateTimeAsString);
	}

	private void ensureRecordExists() {
		if (!recordExists()) {
			setCurrentDateTimeAsLastUpdated();
		}
	}

	private String buildRecordSelectingQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("select %s ", DbFieldNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("from %s", DbTableNames.DB_LAST_UPDATE_TIME));

		return builder.toString();
	}

	private String buildRecordsCountSelectingQuery() {
		return String.format("select count(*) from %s", DbTableNames.DB_LAST_UPDATE_TIME);
	}
}
