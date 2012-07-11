package app.android.gambit.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


final class Preferences
{
	public static final String PREFERENCE_SYNC_GOOGLE_ACCOUNT_NAME = "sync_google_account_name";
	public static final String PREFERENCE_SYNC_SPREADSHEET_KEY = "sync_spreadsheet_key";

	private Preferences() {
	}

	public static void set(Context activityContext, String key, String value) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());
		SharedPreferences.Editor preferencesEditor = preferences.edit();

		preferencesEditor.putString(key, value);

		preferencesEditor.commit();
	}

	public static String get(Context activityContext, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());

		return preferences.getString(key, new String());
	}

	public static void remove(Context activityContext, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());
		SharedPreferences.Editor preferencesEditor = preferences.edit();

		preferencesEditor.remove(key);

		preferencesEditor.commit();
	}
}
