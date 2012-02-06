package app.android.simpleflashcards.ui;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import app.android.simpleflashcards.R;


public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
