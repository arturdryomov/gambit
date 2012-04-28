package app.android.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GambitDbOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Gambit";

	public GambitDbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			db.execSQL(buildDecksTableCreationQuery());
			db.execSQL(buildCardsTableCreationQuery());
			db.execSQL(buildLastUpdateTimeTableCreationQuery());

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private String buildDecksTableCreationQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbTableNames.DECKS));

		builder.append("( ");
		builder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		builder.append(String.format("%s %s, ", DbFieldNames.DECK_TITLE, DbFieldParams.DECK_TITLE));
		builder.append(String.format("%s %s ", DbFieldNames.DECK_CURRENT_CARD_INDEX,
			DbFieldParams.DECK_NEXT_CARD_INDEX));
		builder.append(") ");

		return builder.toString();
	}

	private String buildCardsTableCreationQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbTableNames.CARDS));

		builder.append("(");

		builder.append(String.format(" %s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		builder
			.append(String.format(" %s %s, ", DbFieldNames.CARD_DECK_ID, DbFieldParams.CARD_DECK_ID));
		builder.append(String.format(" %s %s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT,
			DbFieldParams.CARD_FRONT_SIDE_TEXT));
		builder.append(String.format(" %s %s, ", DbFieldNames.CARD_BACK_SIDE_TEXT,
			DbFieldParams.CARD_BACK_SIDE_TEXT));
		builder.append(String.format(" %s %s ", DbFieldNames.CARD_ORDER_INDEX,
			DbFieldParams.CARD_ORDER_INDEX));

		builder.append(")");

		return builder.toString();
	}

	private String buildLastUpdateTimeTableCreationQuery() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("Create table %s ", DbTableNames.DB_LAST_UPDATE_TIME));

		builder.append("( ");
		builder.append(String.format("%s %s ", DbFieldNames.DB_LAST_UPDATE_TIME,
			DbFieldParams.DB_LAST_UPDATE_TIME));
		builder.append(") ");

		return builder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new DatabaseException(String.format(
			"'%s' database is currently not intended to be upgraded", DATABASE_NAME));
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		// See getWritableDatabase() for details
		SQLiteDatabase database = super.getReadableDatabase();
		database.setLockingEnabled(false);
		return database;
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		/* We need to turn off database locking (it's not sqlite stuff, it's part of Android java code).
		 *
		 * When (1) locking is enabled (by default), (2) database transaction is opened,
		 * (3) querying from database is performed and (4) database remains in transaction state for
		 * quite a long period (more than 10 secs) the following error occurs and application crashes:
		 *   'HeapWorker is wedged: 1XXXXms spent inside
		 *   Landroid/database/sqlite/SQLiteCursor;.finalize()V'
		 * See http://stackoverflow.com/q/8570864 for some details.
		 *
		 * It is needed to use transactions when (unit-)testing anything related to database, and
		 * synchronization tests take relatively much time for network operations.
		 *
		 * The only shortcoming from disabling of locking is that SQLiteDatabase client should
		 * guarantee to access database only once at a time. We do not perform concurrent database
		 * queries so it isn't a problem.
		 *
		 * As a side effect SQLiteDatabase#setLockingEnabled(false) may increase database access
		 * performance.
		 */
		SQLiteDatabase database = super.getWritableDatabase();
		database.setLockingEnabled(false);
		return database;
	}
}
