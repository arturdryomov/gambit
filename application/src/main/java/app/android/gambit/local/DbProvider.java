package app.android.gambit.local;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class DbProvider
{
	private class AlreadyInstantiatedException extends RuntimeException
	{
	}

	private static DbProvider instance;

	private Context context;
	private DbOpenHelper databaseOpenHelper;
	private Decks decks;
	private LastUpdateDateTimeHandler lastUpdateDateTimeHandler;

	public static DbProvider getInstance() {
		return instance;
	}

	/**
	 * @throws AlreadyInstantiatedException if this method is called more
	 *  than once.
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

		databaseOpenHelper = new DbOpenHelper(context.getApplicationContext());

		instance = this;

		this.context = context;
	}

	public Decks getDecks() {
		if (decks == null) {
			decks = createDecks();
		}
		return decks;
	}

	private Decks createDecks() {
		Decks decks = new Decks();

		ExampleDeckWriter exampleDeckWriter = new ExampleDeckWriter(context, decks);
		if (exampleDeckWriter.shouldWriteDeck()) {
			exampleDeckWriter.writeDeck();
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
		return databaseOpenHelper.getWritableDatabase();
	}
}
