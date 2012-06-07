package app.android.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DbOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "gambit.db";

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(buildDecksTableCreationQuery());
		db.execSQL(buildCardsTableCreationQuery());
		db.execSQL(buildLastUpdateTimeTableCreationQuery());
	}

	private String buildDecksTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table %s ", DbTableNames.DECKS));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.DECK_TITLE, DbFieldParams.DECK_TITLE));
		queryBuilder.append(
			String.format("%s %s", DbFieldNames.DECK_CURRENT_CARD_INDEX, DbFieldParams.INDEX));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildCardsTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table %s ", DbTableNames.CARDS));

		queryBuilder.append("(");

		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_DECK_ID, DbFieldParams.DECK_FOREIGN_ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldParams.CARD_TEXT));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_BACK_SIDE_TEXT, DbFieldParams.CARD_TEXT));
		queryBuilder.append(String.format("%s %s", DbFieldNames.CARD_ORDER_INDEX, DbFieldParams.INDEX));

		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildLastUpdateTimeTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table %s ", DbTableNames.DB_LAST_UPDATE_TIME));

		queryBuilder.append("(");
		queryBuilder.append(
			String.format("%s %s", DbFieldNames.DB_LAST_UPDATE_TIME, DbFieldParams.DB_LAST_UPDATE_TIME));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
		db.beginTransaction();

		try {
			dropTables(db);
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void dropTables(SQLiteDatabase db) {
		dropTable(db, DbTableNames.DECKS);
		dropTable(db, DbTableNames.CARDS);
		dropTable(db, DbTableNames.DB_LAST_UPDATE_TIME);
	}

	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL(String.format("drop table %s", tableName));
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		/*
		 * We need to turn off database locking in order to avoid “HeapWorker is wedged:
		 * 1XXXXms spent inside Landroid/database/sqlite/SQLiteCursor;.finalize()V” error.
		 */
		SQLiteDatabase database = super.getReadableDatabase();
		database.setLockingEnabled(false);
		return database;
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase database = super.getWritableDatabase();
		database.setLockingEnabled(false);
		return database;
	}
}
