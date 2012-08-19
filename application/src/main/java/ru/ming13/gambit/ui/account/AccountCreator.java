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


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;


public class AccountCreator
{
	private static final String ACCOUNT_TYPE = "com.google";
	private static final String ACCOUNT_SIGN_UP_TOKEN_TYPE = "ah";

	private final Activity activity;

	/**
	 * @throws SignUpCanceledException if user cancelled sign up.
	 * @throws SignUpFailedException if an error occurred during sign up.
	 */
	public static Account create(Activity activity) {
		return new AccountCreator(activity).create();
	}

	private AccountCreator(Activity activity) {
		this.activity = activity;
	}

	private Account create() {
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
			throw new SignUpFailedException();
		}
		catch (IOException e) {
			throw new SignUpFailedException();
		}

		return extractAccount(signUpResponse);
	}

	private Account extractAccount(Bundle signUpResponse) {
		if (!signUpResponse.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
			throw new SignUpFailedException();
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

		throw new SignUpFailedException();
	}
}
