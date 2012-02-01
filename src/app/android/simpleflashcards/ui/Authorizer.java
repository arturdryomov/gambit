package app.android.simpleflashcards.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import app.android.simpleflashcards.R;

public class Authorizer
{
	// Defined by API
	private static final String SPREADSHEETS_AUTH_TOKEN_TYPE = "wise";
	private static final String DOCUMENTS_LIST_AUTH_TOKEN_TYPE = "writely";

	private static final String GOOGLE_ACCOUNT_TYPE = "com.google";
	private static final String PREFERENCE_GOOGLE_ACCOUNT_NAME = "google_account";

	private AuthTokenWaiter tokenWaiter;
	private final Activity activity;
	private String authType;

	public static enum ServiceType {
		SPREADSHEETS, DOCUMENTS_LIST
	}

	public Authorizer(Activity activity) {
		this.activity = activity;
	}

	public void authorize(ServiceType serviceType, AuthTokenWaiter tokenWaiter) {
		this.authType = authTypeFromServiceType(serviceType);
		this.tokenWaiter = tokenWaiter;

		authorize();
	}

	private String authTypeFromServiceType(ServiceType serviceType) {
		switch (serviceType) {
			case SPREADSHEETS:
				return SPREADSHEETS_AUTH_TOKEN_TYPE;

			case DOCUMENTS_LIST:
				return DOCUMENTS_LIST_AUTH_TOKEN_TYPE;

			default:
				throw new RuntimeException("Unknown service type");
		}
	}

	private void authorize() {
		if (haveAccountNameInPreferences()) {
			String accountName = loadAccountNameFromPreferences();

			if (haveAccountRegistered(accountName)) {
				authorizeForAccount(loadAccountNameFromPreferences());
			}
			else {
				selectAccount();
			}
		}
		else {
			selectAccount();
		}
	}

	private boolean haveAccountNameInPreferences() {
		String accountName = loadAccountNameFromPreferences();
		return !accountName.isEmpty();
	}

	private String loadAccountNameFromPreferences() {
		return getPreference(PREFERENCE_GOOGLE_ACCOUNT_NAME);
	}

	private boolean haveAccountRegistered(String accountName) {
		return getAccountsNames().contains(accountName);
	}

	private void selectAccount() {
		if (haveSomeAccountsRegistered()) {
			constructAccountsListDialog().show();
		}
		else {
			UserAlerter.alert(activity, activity.getString(R.string.noGoogleAccounts));
		}
	}

	private boolean haveSomeAccountsRegistered() {
		return !getAccountsNames().isEmpty();
	}

	private List<String> getAccountsNames() {
		List<String> result = new ArrayList<String>();

		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

		for (Account account : accountManager.getAccountsByType(GOOGLE_ACCOUNT_TYPE)) {
			result.add(account.name);
		}

		return result;
	}

	private Dialog constructAccountsListDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getString(R.string.chooseGoogleAccount));

		final List<String> accountsNamesList = getAccountsNames();
		String[] accountsNamesArray = new String[accountsNamesList.size()];
		accountsNamesList.toArray(accountsNamesArray);

		dialogBuilder.setItems(accountsNamesArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selectedAccountName = accountsNamesList.get(which);

				setPreference(PREFERENCE_GOOGLE_ACCOUNT_NAME, selectedAccountName);

				authorizeForAccount(selectedAccountName);
			}
		});

		return dialogBuilder.create();
	}

	private String getPreference(String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity
			.getApplicationContext());

		return preferences.getString(key, new String());
	}

	private void setPreference(String key, String value) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity
			.getApplicationContext());
		SharedPreferences.Editor preferenceEditor = preferences.edit();

		preferenceEditor.putString(key, value);

		preferenceEditor.commit();
	}

	private void authorizeForAccount(String accountName) {
		new AuthorizeTask(accountName).execute();
	}

	private class AuthorizeTask extends AsyncTask<Void, Void, String>
	{
		private final Account account;

		private String authToken;

		public AuthorizeTask(String accountName) {
			super();

			this.account = accountFromName(accountName);

			authToken = new String();
		}

		private Account accountFromName(String accountName) {
			AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

			for (Account account : accountManager.getAccountsByType(GOOGLE_ACCOUNT_TYPE)) {
				if (account.name.equals(accountName)) {
					return account;
				}
			}

			return null;
		}

		@Override
		protected String doInBackground(Void... params) {
			AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

			Bundle authResponse = new Bundle();

			try {
				authResponse = accountManager.getAuthToken(account, authType, null, activity, null, null)
					.getResult();
			}
			catch (OperationCanceledException e) {
				return activity.getString(R.string.authenticationCanceled);
			}
			catch (AuthenticatorException e) {
				return activity.getString(R.string.authenticationError);
			}
			catch (IOException e) {
				return activity.getString(R.string.someError);
			}

			if (authResponse.containsKey(AccountManager.KEY_AUTHTOKEN)) {
				authToken = authResponse.getString(AccountManager.KEY_AUTHTOKEN);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (errorMessage.isEmpty()) {
				tokenReceived(authToken);
			}
			else {
				UserAlerter.alert(activity, errorMessage);
			}
		}
	}

	private void tokenReceived(String authToken) {
		tokenWaiter.onTokenReceived(authToken);
	}
}
