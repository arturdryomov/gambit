package app.android.gambit.ui;


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;


class AccountCreator
{
	private static final String ACCOUNT_TYPE = "com.google";
	private static final String ACCOUNT_SIGN_UP_TOKEN_TYPE = "ah";

	private final Activity activity;

	/**
	 * @throws SignUpCanceledException if user cancelled sign up.
	 */
	public static Account create(Activity activity) {
		return new AccountCreator(activity).createAccount();
	}

	public AccountCreator(Activity activity) {
		this.activity = activity;
	}

	private Account createAccount() {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
		Bundle signUpResponse;

		try {
			signUpResponse = accountManager.addAccount(ACCOUNT_TYPE, ACCOUNT_SIGN_UP_TOKEN_TYPE, null,
				null, activity, null, null).getResult();
		}
		catch (OperationCanceledException e) {
			throw new SignUpCanceledException();
		}
		catch (AuthenticatorException e) {
			throw new SignUpCanceledException();
		}
		catch (IOException e) {
			throw new SignUpCanceledException();
		}

		if (!signUpResponse.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
			throw new SignUpCanceledException();
		}

		String accountName = signUpResponse.getString(AccountManager.KEY_ACCOUNT_NAME);
		return buildAccountFromName(accountName);
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
}
