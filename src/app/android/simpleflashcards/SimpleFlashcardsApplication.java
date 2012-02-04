package app.android.simpleflashcards;


import android.app.Application;
import app.android.simpleflashcards.models.DatabaseProvider;


public class SimpleFlashcardsApplication extends Application
{
	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseProvider.getInstance(this);
	}
}
