package app.android.simpleflashcards;


import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import app.android.simpleflashcards.models.Decks;
import app.android.simpleflashcards.models.SimpleFlashcardsOpenHelper;


public class SimpleFlashcardsApplication extends Application
{
	private static SimpleFlashcardsApplication instance = null;
	private SQLiteDatabase database;
	private Decks decks;

	@Override
	public void onCreate() {
		super.onCreate();

		if (instance != null) {
			throw new RuntimeException(String.format("%s can have only one instance",
				SimpleFlashcardsApplication.class.toString()));
		}
		instance = this;

		SQLiteOpenHelper openHelper = new SimpleFlashcardsOpenHelper(this);
		database = openHelper.getWritableDatabase();
		decks = new Decks(database);
	}

	public Decks getDecks() {
		return decks;
	}

	public static SimpleFlashcardsApplication getInstance() {
		return instance;
	}
}
