package app.android.simpleflashcards;


import android.app.Application;
import app.android.simpleflashcards.models.DatabaseProvider;
import app.android.simpleflashcards.models.Decks;


public class SimpleFlashcardsApplication extends Application
{
	private static SimpleFlashcardsApplication instance = null;
	private Decks decks;

	@Override
	public void onCreate() {
		super.onCreate();

		if (instance != null) {
			throw new RuntimeException(String.format("%s can have only one instance",
				SimpleFlashcardsApplication.class.toString()));
		}
		instance = this;

		DatabaseProvider provider = DatabaseProvider.getInstance(this);
		decks = provider.getDecks();
	}

	public Decks getDecks() {
		return decks;
	}

	public static SimpleFlashcardsApplication getInstance() {
		return instance;
	}
}
