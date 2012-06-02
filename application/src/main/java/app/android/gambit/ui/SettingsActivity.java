package app.android.gambit.ui;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import app.android.gambit.R;


public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
