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

	public static void set(Context context, String key, String value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.putString(key, value);

		preferencesEditor.commit();
	}

	private static SharedPreferences.Editor getPreferencesEditor(Context context) {
		return getSharedPreferences(context).edit();
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public static void set(Context context, String key, boolean value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.putBoolean(key, value);

		preferencesEditor.commit();
	}

	public static String getString(Context context, String key) {
		return getSharedPreferences(context).getString(key, new String());
	}

	public static boolean getBoolean(Context context, String key) {
		return getSharedPreferences(context).getBoolean(key, false);
	}
}
