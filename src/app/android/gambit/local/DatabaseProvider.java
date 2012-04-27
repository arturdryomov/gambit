package app.android.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseProvider
{
	public class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DatabaseProvider instance;
	private GambitDbOpenHelper openHelper;
	private Decks decks;
	private LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	public static DatabaseProvider getInstance() {
		return instance;
	}

	/**
	 * @throws AlreadyInstantiatedException if this method is called more
	 * than once.
	 */
	public static DatabaseProvider getInstance(Context context) {
		if (instance == null) {
			return new DatabaseProvider(context);
		}
		else {
			return instance;
		}
	}

	private DatabaseProvider(Context context) {
		if (instance != null) {
			throw new AlreadyInstantiatedException();
		}

		openHelper = new GambitDbOpenHelper(context.getApplicationContext());

		instance = this;
	}

	public Decks getDecks() {
		if (decks == null) {
			decks = new Decks();
		}
		return decks;
	}

	LastUpdateDateTimeHandler getLastUpdateTimeHandler() {
		if (lastUpdateDateTimeHandler == null) {
			lastUpdateDateTimeHandler = new LastUpdateDateTimeHandler();
		}
		return lastUpdateDateTimeHandler;
	}

	SQLiteDatabase getDatabase() {
		return openHelper.getWritableDatabase();
	}
}
