package app.android.gambit.ui;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import app.android.gambit.R;
import app.android.gambit.local.DbProvider;
import app.android.gambit.remote.EntryNotFoundException;
import app.android.gambit.remote.FailedRequestException;
import app.android.gambit.remote.Synchronizer;
import app.android.gambit.remote.UnauthorizedException;


class SyncOperator extends AsyncTask<Void, Void, String>
{
	private final Context activityContext;
	private final Activity activity;

	private final Runnable successRunnable;

	private String documentsListAuthToken;
	private String spreadsheetsAuthToken;

	private boolean isTokensInvalidationRequired;

	private ProgressDialogShowHelper progressDialogShowHelper;

	public SyncOperator(Context activityContext, Runnable successRunnable) {
		this.activityContext = activityContext;
		this.activity = (Activity) activityContext;

		this.successRunnable = successRunnable;

		isTokensInvalidationRequired = false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialogShowHelper = new ProgressDialogShowHelper();
		progressDialogShowHelper.show(activityContext, R.string.loading_sync);
	}

	@Override
	protected String doInBackground(Void... params) {
		String authorizationErrorMessage = authorize();
		if (!authorizationErrorMessage.isEmpty()) {
			return authorizationErrorMessage;
		}

		return sync();
	}

	private String authorize() {
		try {
			Account account = getAccount();

			getAuthTokens(account);
			if (isTokensInvalidationRequired) {
				invalidateAuthTokens(account);
			}
		}
		catch (SignUpCanceledException e) {
			// Skip sign up cancel, user already knows that he did it
		}
		catch (SignUpFailedException e) {
			return activityContext.getString(R.string.error_network);
		}
		catch (AuthorizationCanceledException e) {
			// Skip revoking auth access, user already knows that he did it
		}
		catch (AuthorizationFailedException e) {
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

	private String sync() {
		try {
			if (!haveSyncSpreadsheetKeyInPreferences()) {
				syncFirstTime();
			}
			else {
				syncNotFirstTime();
			}
		}
		catch (UnauthorizedException e) {
			if (!isTokensInvalidationRequired) {
				// Run again and invalidate tokens
				isTokensInvalidationRequired = true;
				doInBackground();
			}
			else {
				// Invalidation failed
				return activityContext.getString(R.string.error_unspecified);
			}
		}
		catch (EntryNotFoundException e) {
			// Run again and force find or create spreadsheet
			removeSyncSpreadsheetKeyFromPreferences();
			return sync();
		}
		catch (FailedRequestException e) {
			return activityContext.getString(R.string.error_network);
		}

		return new String();
	}

	private boolean haveSyncSpreadsheetKeyInPreferences() {
		return !loadSyncSpreadsheetKeyFromPreferences().isEmpty();
	}

	private String loadSyncSpreadsheetKeyFromPreferences() {
		return PreferencesOperator.get(activityContext,
			PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY);
	}

	private void syncFirstTime() {
		Synchronizer synchronizer = new Synchronizer();

		String syncSpreadsheetKey;

		try {
			syncSpreadsheetKey = synchronizer.getExistingSpreadsheetKey(documentsListAuthToken);

			// Local storage is empty, so try to download remote
			if (DbProvider.getInstance().getDecks().getDecksList().size() == 0) {
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

	private void saveSyncSpreadsheetKeyToPreferences(String syncSpreadsheetKey) {
		PreferencesOperator.set(activityContext, PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY,
			syncSpreadsheetKey);
	}

	private void syncNotFirstTime() {
		Synchronizer synchronizer = new Synchronizer();
		synchronizer.synchronize(loadSyncSpreadsheetKeyFromPreferences(), spreadsheetsAuthToken);
	}

	private void removeSyncSpreadsheetKeyFromPreferences() {
		PreferencesOperator.remove(activityContext,
			PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY);
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