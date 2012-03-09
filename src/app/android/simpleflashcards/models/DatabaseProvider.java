package app.android.simpleflashcards.models;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DatabaseProvider
{
	public class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DatabaseProvider instance;
	private SimpleFlashcardsOpenHelper openHelper;
	private Decks decks;
	private LastUpdateTimeHandler lastUpdateTimeHandler;

	public static DatabaseProvider getInstance() {
		return instance;
	}

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

		openHelper = new SimpleFlashcardsOpenHelper(context.getApplicationContext());

		instance = this;
	}

	public Decks getDecks() {
		if (decks == null) {
			decks = new Decks();
		}
		return decks;
	}

	LastUpdateTimeHandler getLastUpdateTimeHandler() {
		if (lastUpdateTimeHandler == null) {
			lastUpdateTimeHandler = new LastUpdateTimeHandler();
		}
		return lastUpdateTimeHandler;
	}

	SQLiteDatabase getDatabase() {
		return openHelper.getWritableDatabase();
	}
}
