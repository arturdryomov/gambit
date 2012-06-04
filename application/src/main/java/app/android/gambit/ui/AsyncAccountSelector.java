package app.android.gambit.ui;


import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import app.android.gambit.R;


public class AsyncAccountSelector
{
	public static enum Result {
		SUCCESS, NO_ACCOUNTS_REGISTERED
	}

	public static interface AccountWaiter
	{
		void onAccountObtained(Result result, Account account);
	}

	private static final String ACCOUNT_NAME = "google_account";
	private static final String ACCOUNT_TYPE = "com.google";

	private final Activity activity;
	private final AccountWaiter accountWaiter;

	public AsyncAccountSelector(Activity selectDialogParent, AccountWaiter accountWaiter) {
		this.activity = selectDialogParent;
		this.accountWaiter = accountWaiter;
	}

	public void selectAccount() {
		if (haveAccountNameInPreferences()) {
			notifyAccountWaiter(loadAccountNameFromPreferences());
		}
		else {
			selectAccountFromDialog();
		}
	}

	private boolean haveAccountNameInPreferences() {
		String accountName = loadAccountNameFromPreferences();
		return !accountName.isEmpty();
	}

	private String loadAccountNameFromPreferences() {
		String accountName = getPreference(ACCOUNT_NAME);

		if (isAccountRegistered(accountName)) {
			return accountName;
		}
		else {
			return new String();
		}
	}

	private String getPreference(String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity
			.getApplicationContext());

		return preferences.getString(key, new String());
	}

	private boolean isAccountRegistered(String accountName) {
		return getRegisteredAccountsNames().contains(accountName);
	}

	private List<String> getRegisteredAccountsNames() {
		List<String> registeredAccountsNames = new ArrayList<String>();

		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

		for (Account account : accountManager.getAccountsByType(ACCOUNT_TYPE)) {
			registeredAccountsNames.add(account.name);
		}

		return registeredAccountsNames;
	}

	private void notifyAccountWaiter(String accountName) {
		Account account = buildAccountFromName(accountName);

		Result result;
		if (account == null) {
			result = Result.NO_ACCOUNTS_REGISTERED;
		}
		else {
			result = Result.SUCCESS;
		}

		accountWaiter.onAccountObtained(result, account);
	}

	private Account buildAccountFromName(String accountName) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

		for (Account account : accountManager.getAccountsByType(ACCOUNT_TYPE)) {
			if (account.name.equals(accountName)) {
				return account;
			}
		}

		return null;
	}

	private void selectAccountFromDialog() {
		if (haveSomeAccountRegistered()) {
			constructAccountsListDialog().show();
		}
		else {
			notifyAccountWaiter(new String());
		}
	}

	private boolean haveSomeAccountRegistered() {
		return !getRegisteredAccountsNames().isEmpty();
	}

	private Dialog constructAccountsListDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getString(R.string.title_choose_google_account));

		final List<String> accountsNamesList = getRegisteredAccountsNames();
		String[] accountsNamesArray = new String[accountsNamesList.size()];
		accountsNamesList.toArray(accountsNamesArray);

		dialogBuilder.setItems(accountsNamesArray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int selectedAccountPosition) {
				String selectedAccountName = accountsNamesList.get(selectedAccountPosition);

				setPreference(ACCOUNT_NAME, selectedAccountName);

				notifyAccountWaiter(selectedAccountName);
			}
		});

		return dialogBuilder.create();
	}

	private void setPreference(String key, String value) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity
			.getApplicationContext());
		SharedPreferences.Editor preferencesEditor = preferences.edit();

		preferencesEditor.putString(key, value);

		preferencesEditor.commit();
	}
}
