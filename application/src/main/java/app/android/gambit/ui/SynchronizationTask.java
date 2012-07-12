package app.android.gambit.ui;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import app.android.gambit.R;
import app.android.gambit.remote.NothingToSyncException;
import app.android.gambit.remote.SyncException;
import app.android.gambit.remote.Synchronizer;
import app.android.gambit.remote.UnauthorizedException;


class SynchronizationTask extends AsyncTask<Void, Void, String>
{
	private final Activity activity;

	private final Runnable successRunnable;

	private String driveAuthToken;
	private String apiKey;

	private boolean isTokensInvalidationRequired;

	private ProgressDialogHelper progressDialogHelper;

	public SynchronizationTask(Context activityContext, Runnable successRunnable) {
		this.activity = (Activity) activityContext;
		this.successRunnable = successRunnable;

		isTokensInvalidationRequired = false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialogHelper = new ProgressDialogHelper();
		progressDialogHelper.show(activity, R.string.loading_sync);
	}

	@Override
	protected String doInBackground(Void... params) {
		String authorizationErrorMessage = authorize();
		if (!TextUtils.isEmpty(authorizationErrorMessage)) {
			return authorizationErrorMessage;
		}

		return sync();
	}

	private String authorize() {
		try {
			Account account = getAccount();

			getApiKey();
			getAuthToken(account);
			if (isTokensInvalidationRequired) {
				invalidateAuthToken(account);
			}
		}
		catch (SignUpCanceledException e) {
			// Skip sign up cancel, user already knows that he did it
		}
		catch (SignUpFailedException e) {
			return activity.getString(R.string.error_unspecified);
		}
		catch (AuthorizationCanceledException e) {
			// Skip revoking auth access, user already knows that he did it
		}
		catch (AuthorizationFailedException e) {
			return activity.getString(R.string.error_unspecified);
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

	private void getApiKey() {
		apiKey = activity.getString(R.string.google_api_key);
	}

	private void getAuthToken(Account account) {
		GoogleDriveAuthorizer googleDriveAuthorizer = new GoogleDriveAuthorizer(activity);

		driveAuthToken = googleDriveAuthorizer.getToken(account);
	}

	private void invalidateAuthToken(Account account) {
		GoogleDriveAuthorizer googleDriveAuthorizer = new GoogleDriveAuthorizer(activity);

		googleDriveAuthorizer.invalidateToken(driveAuthToken);

		getAuthToken(account);
	}

	private String sync() {
		try {
			trySync();
		}
		catch (NothingToSyncException e) {
			// No local and no remote data means equal data and successful sync
			return new String();
		}
		catch (UnauthorizedException e) {
			if (isTokensInvalidationRequired) {
				return activity.getString(R.string.error_unspecified);
			}
			else {
				isTokensInvalidationRequired = true;
				doInBackground();
			}
		}
		catch (SyncException e) {
			return activity.getString(R.string.error_unspecified);
		}

		return new String();
	}

	private void trySync() {
		Synchronizer synchronizer = new Synchronizer(driveAuthToken, apiKey);
		String spreadsheetKey;

		if (!haveSyncSpreadsheetKeyInPreferences()) {
			spreadsheetKey = synchronizer.sync();
		}
		else {
			spreadsheetKey = synchronizer.sync(loadSyncSpreadsheetKeyFromPreferences());
		}

		saveSyncSpreadsheetKeyToPreferences(spreadsheetKey);
	}

	private boolean haveSyncSpreadsheetKeyInPreferences() {
		return !TextUtils.isEmpty(loadSyncSpreadsheetKeyFromPreferences());
	}

	private String loadSyncSpreadsheetKeyFromPreferences() {
		return Preferences.get(activity, Preferences.PREFERENCE_SYNC_SPREADSHEET_KEY);
	}

	private void saveSyncSpreadsheetKeyToPreferences(String syncSpreadsheetKey) {
		Preferences.set(activity, Preferences.PREFERENCE_SYNC_SPREADSHEET_KEY, syncSpreadsheetKey);
	}

	@Override
	protected void onPostExecute(String errorMessage) {
		super.onPostExecute(errorMessage);

		progressDialogHelper.hide();

		if (!TextUtils.isEmpty(errorMessage)) {
			UserAlerter.alert(activity, errorMessage);
		}
		else {
			successRunnable.run();
		}
	}
}
