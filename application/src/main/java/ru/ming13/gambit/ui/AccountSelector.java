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

package ru.ming13.gambit.ui;


import java.util.concurrent.CountDownLatch;

import android.accounts.Account;
import android.app.Activity;


public class AccountSelector
{
	private final Activity activity;
	private final CountDownLatch singleOperationLatch = new CountDownLatch(1);

	private Account selectedAccount;
	private AsyncAccountSelector.Result accountSelectionResult;

	/**
	 * @throws NoAccountRegisteredException if there is no registered accounts
	 *  and user has nothing to select from.
	 */
	public static Account select(Activity activity) {
		return new AccountSelector(activity).select();
	}

	private AccountSelector(Activity activity) {
		this.activity = activity;
	}

	private Account select() {
		startAccountSelection();
		waitForAccountSelection();

		return getSelectedAccount();
	}

	private void startAccountSelection() {
		final AsyncAccountSelector asyncAccountSelector = new AsyncAccountSelector(activity,
			new AccountWaiter());

		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				asyncAccountSelector.selectAccount();
			}
		});
	}

	private class AccountWaiter implements AsyncAccountSelector.AccountWaiter
	{
		@Override
		public void onAccountObtained(AsyncAccountSelector.Result result, Account account) {
			accountSelectionResult = result;
			selectedAccount = account;

			singleOperationLatch.countDown();
		}
	}

	private void waitForAccountSelection() {
		try {
			singleOperationLatch.await();
		}
		catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	private Account getSelectedAccount() {
		if (accountSelectionResult == AsyncAccountSelector.Result.NO_ACCOUNTS_REGISTERED) {
			throw new NoAccountRegisteredException();
		}

		return selectedAccount;
	}
}
