/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.ui.account;


import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import org.apache.commons.lang.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.util.Preferences;


public class AsyncAccountSelector
{
	public static enum Result
	{
		SUCCESS, NO_ACCOUNTS_REGISTERED
	}

	public static interface AccountWaiter
	{
		void onAccountObtained(Result result, Account account);
	}

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
		else if (isSingleAccountRegistered()) {
			String accountName = getSingleRegisteredAccount();

			saveAccountNameToPreferences(accountName);
			notifyAccountWaiter(accountName);
		}
		else {
			selectAccountFromDialog();
		}
	}

	private boolean haveAccountNameInPreferences() {
		String accountName = loadAccountNameFromPreferences();

		return StringUtils.isNotBlank(accountName);
	}

	private String loadAccountNameFromPreferences() {
		String accountName = Preferences.getString(activity, Preferences.Keys.SYNC_GOOGLE_ACCOUNT_NAME);

		if (isAccountRegistered(accountName)) {
			return accountName;
		}
		else {
			return StringUtils.EMPTY;
		}
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

	private boolean isSingleAccountRegistered() {
		return getRegisteredAccountsNames().size() == 1;
	}

	private String getSingleRegisteredAccount() {
		final int SINGLE_ACCOUNT_INDEX = 0;

		return getRegisteredAccountsNames().get(SINGLE_ACCOUNT_INDEX);
	}

	private void saveAccountNameToPreferences(String accountName) {
		Preferences.set(activity, Preferences.Keys.SYNC_GOOGLE_ACCOUNT_NAME, accountName);
	}

	private void selectAccountFromDialog() {
		if (haveSomeAccountRegistered()) {
			buildAccountsListDialog().show();
		}
		else {
			notifyAccountWaiter(StringUtils.EMPTY);
		}
	}

	private boolean haveSomeAccountRegistered() {
		return !getRegisteredAccountsNames().isEmpty();
	}

	private Dialog buildAccountsListDialog() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(activity.getString(R.string.title_choose_google_account));

		final List<String> accountsNamesList = getRegisteredAccountsNames();
		String[] accountsNamesArray = new String[accountsNamesList.size()];
		accountsNamesList.toArray(accountsNamesArray);

		dialogBuilder.setItems(accountsNamesArray, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int selectedAccountPosition) {
				String selectedAccountName = accountsNamesList.get(selectedAccountPosition);

				saveAccountNameToPreferences(selectedAccountName);
				notifyAccountWaiter(selectedAccountName);
			}
		});

		return dialogBuilder.create();
	}
}
