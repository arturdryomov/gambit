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
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(activityContext);

		preferencesEditor.putString(key, value);

		preferencesEditor.commit();
	}

	private static SharedPreferences.Editor getPreferencesEditor(Context activityContext) {
		return getSharedPreferences(activityContext).edit();
	}

	private static SharedPreferences getSharedPreferences(Context activityContext) {
		return PreferenceManager.getDefaultSharedPreferences(activityContext.getApplicationContext());
	}

	public static void set(Context activityContext, String key, boolean value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(activityContext);

		preferencesEditor.putBoolean(key, value);

		preferencesEditor.commit();
	}

	public static String getString(Context activityContext, String key) {
		return getSharedPreferences(activityContext).getString(key, new String());
	}

	public static boolean getBoolean(Context activityContext, String key) {
		return getSharedPreferences(activityContext).getBoolean(key, false);
	}
}
