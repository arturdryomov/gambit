package app.android.gambit.ui;


import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import app.android.gambit.R;


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
		if (!TextUtils.isEmpty(authorizationErrorMessage)) {
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
			return activityContext.getString(R.string.error_unspecified);
		}
		catch (AuthorizationCanceledException e) {
			// Skip revoking auth access, user already knows that he did it
		}
		catch (AuthorizationFailedException e) {
			return activityContext.getString(R.string.error_unspecified);
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
		if (!haveSyncSpreadsheetKeyInPreferences()) {
			// TODO: Sync with key
		}
		else {
			// TODO: Sync without key
		}

		return new String();
	}

	private boolean haveSyncSpreadsheetKeyInPreferences() {
		return !TextUtils.isEmpty(loadSyncSpreadsheetKeyFromPreferences());
	}

	private String loadSyncSpreadsheetKeyFromPreferences() {
		return PreferencesOperator.get(activityContext,
			PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY);
	}

	private void saveSyncSpreadsheetKeyToPreferences(String syncSpreadsheetKey) {
		PreferencesOperator.set(activityContext, PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY,
			syncSpreadsheetKey);
	}

	private void removeSyncSpreadsheetKeyFromPreferences() {
		PreferencesOperator.remove(activityContext,
			PreferencesOperator.PREFERENCE_SYNC_SPREADSHEET_KEY);
	}

	@Override
	protected void onPostExecute(String errorMessage) {
		super.onPostExecute(errorMessage);

		progressDialogShowHelper.hide();

		if (!TextUtils.isEmpty(errorMessage)) {
			UserAlerter.alert(activityContext, errorMessage);
		}
		else {
			successRunnable.run();
		}
	}
}