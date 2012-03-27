package app.android.simpleflashcards.models;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.android.simpleflashcards.InternetDateTime;


class LastUpdateDateTimeHandler
{
	private final SQLiteDatabase database;

	public LastUpdateDateTimeHandler() {
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

		updateRecord(new InternetDateTime());
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
		builder.append(String.format("values (%s)", "''"));

		return builder.toString();
	}

	private void updateRecord(InternetDateTime dateTime) {
		database.execSQL(buildRecordUpdatingQuery(dateTime));
	}

	private String buildRecordUpdatingQuery(InternetDateTime dateTime) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("update %s ", DbTableNames.DB_LAST_UPDATE_TIME));
		builder.append(String.format("set %s='%s' ", DbFieldNames.DB_LAST_UPDATE_TIME,
			dateTime.toString()));

		return builder.toString();
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

		Cursor cursor = database.rawQuery(buildRecordSelectingQuery(), null);
		cursor.moveToFirst();

		String dateTimeAsString = cursor.getString(0);

		return new InternetDateTime(dateTimeAsString);
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
