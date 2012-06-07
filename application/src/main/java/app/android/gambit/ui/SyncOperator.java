package app.android.gambit.ui;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import app.android.gambit.R;
import app.android.gambit.local.DbProvider;
import app.android.gambit.remote.EntryNotFoundException;
import app.android.gambit.remote.FailedRequestException;
import app.android.gambit.remote.Synchronizer;
import app.android.gambit.remote.UnauthorizedException;


class SyncOperator extends AsyncTask<Void, Void, String>
{
	private static final String PREFERENCE_SYNC_SPREADSHEET_KEY = "sync_spreadsheet_key";

	private ProgressDialogShowHelper progressDialogShowHelper;

	private String documentsListAuthToken;
	private String spreadsheetsAuthToken;

	private boolean isTokensInvalidationEnabled = false;

	private final Context activityContext;
	private final Activity activity;

	private final Runnable successRunnable;

	public SyncOperator(Context activityContext, Runnable successRunnable) {
		this.activityContext = activityContext;
		this.activity = (Activity) activityContext;

		this.successRunnable = successRunnable;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialogShowHelper = new ProgressDialogShowHelper();
		progressDialogShowHelper.show(activityContext, R.string.loading_sync);
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			Account account = getAccount();

			getAuthTokens(account);
			if (isTokensInvalidationEnabled) {
				invalidateAuthTokens(account);
			}

			if (!isSyncSpreadsheetKeyInPreferences()) {
				syncFirstTime();
			}
			else {
				syncNotFirstTime();
			}
		}
		catch (SignUpCanceledException e) {
			// Skip sign up cancel, user already knows that he did it
		}
		catch (AuthorizationCanceledException e) {
			// Skip revoking auth access, user already knows that he did it
		}
		catch (AuthorizationFailedException e) {
			return activityContext.getString(R.string.error_unspecified);
		}
		catch (UnauthorizedException e) {
			if (isTokensInvalidationEnabled) {
				return activityContext.getString(R.string.error_unspecified);
			}
			else {
				// Run again and invalidate tokens
				isTokensInvalidationEnabled = true;
				doInBackground();
			}
		}
		catch (EntryNotFoundException e) {
			// Run again and force find or create spreadsheet
			removeSyncSpreadsheetKey();
			doInBackground();
		}
		catch (FailedRequestException e) {
			return activityContext.getString(R.string.error_network);
		}

		return new String();
	}

	private Account getAccount() {
		try {
			return AccountSelector.select(activity);
		}
		catch (NoAccountRegisteredException e) {
			return AccountCreator.create(activity);
		}
	}

	private void getAuthTokens(Account account) {
		Authorizer authorizer = new Authorizer(activity);

		documentsListAuthToken = authorizer.getToken(Authorizer.ServiceType.DOCUMENTS_LIST, account);
		spreadsheetsAuthToken = authorizer.getToken(Authorizer.ServiceType.SPREADSHEETS, account);
	}

	private void invalidateAuthTokens(Account account) {
		Authorizer authorizer = new Authorizer(activity);

		authorizer.invalidateToken(documentsListAuthToken);
		authorizer.invalidateToken(spreadsheetsAuthToken);

		getAuthTokens(account);
	}

	private void syncFirstTime() {
		Synchronizer synchronizer = new Synchronizer();

		String syncSpreadsheetKey;

		try {
			syncSpreadsheetKey = synchronizer.getExistingSpreadsheetKey(documentsListAuthToken);

			// Local storage is empty, so try to download remote
			if (DbProvider.getInstance().getDecks().getDecksCount() == 0) {
				synchronizer.syncFromRemoteToLocal(syncSpreadsheetKey, spreadsheetsAuthToken);
			}
		}
		catch (EntryNotFoundException e) {
			syncSpreadsheetKey = synchronizer.createSpreadsheet(documentsListAuthToken);

			// Remote storage is empty, so try to upload local
			synchronizer.syncFromLocalToRemote(syncSpreadsheetKey, spreadsheetsAuthToken);
		}

		saveSyncSpreadsheetKeyToPreferences(syncSpreadsheetKey);
	}

	private void syncNotFirstTime() {
		Synchronizer synchronizer = new Synchronizer();
		synchronizer.synchronize(loadSyncSpreadsheetKeyFromPreferences(), spreadsheetsAuthToken);
	}

	private boolean isSyncSpreadsheetKeyInPreferences() {
		return !loadSyncSpreadsheetKeyFromPreferences().isEmpty();
	}

	private String loadSyncSpreadsheetKeyFromPreferences() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());

		return preferences.getString(PREFERENCE_SYNC_SPREADSHEET_KEY, new String());
	}

	private void saveSyncSpreadsheetKeyToPreferences(String syncSpreadsheetKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());
		SharedPreferences.Editor preferencesEditor = preferences.edit();

		preferencesEditor.putString(PREFERENCE_SYNC_SPREADSHEET_KEY, syncSpreadsheetKey);

		preferencesEditor.commit();
	}

	private void removeSyncSpreadsheetKey() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
			activityContext.getApplicationContext());
		SharedPreferences.Editor preferencesEditor = preferences.edit();

		preferencesEditor.remove(PREFERENCE_SYNC_SPREADSHEET_KEY);

		preferencesEditor.commit();
	}

	@Override
	protected void onPostExecute(String errorMessage) {
		super.onPostExecute(errorMessage);

		progressDialogShowHelper.hide();

		if (!errorMessage.isEmpty()) {
			UserAlerter.alert(activityContext, errorMessage);
		}
		else {
			successRunnable.run();
		}
	}
}