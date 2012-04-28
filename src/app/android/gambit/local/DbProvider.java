package app.android.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbProvider
{
	public class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DbProvider instance;
	private DbOpenHelper openHelper;
	private Decks decks;
	private LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	public static DbProvider getInstance() {
		return instance;
	}

	/**
	 * @throws AlreadyInstantiatedException if this method is called more
	 * than once.
	 */
	public static DbProvider getInstance(Context context) {
		if (instance == null) {
			return new DbProvider(context);
		}
		else {
			return instance;
		}
	}

	private DbProvider(Context context) {
		if (instance != null) {
			throw new AlreadyInstantiatedException();
		}

		openHelper = new DbOpenHelper(context.getApplicationContext());

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
