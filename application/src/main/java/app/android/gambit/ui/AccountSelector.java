package app.android.gambit.ui;


import java.util.concurrent.CountDownLatch;

import android.accounts.Account;
import android.app.Activity;


public class AccountSelector
{
	private final Activity activity;
	private final CountDownLatch latch = new CountDownLatch(1);

	private Account obtainedAccount;
	private AsyncAccountSelector.Result accountSelectionResult;

	public static Account select(Activity dialogParentActivity) {
		return new AccountSelector(dialogParentActivity).select();
	}

	private AccountSelector(Activity dialogParentActivity) {
		activity = dialogParentActivity;
	}

	/**
	 *	@throws NoAccountRegisteredException if there is no registered accounts
	 *		and user has nothing to select from.
	 */
	private Account select() {
		final AsyncAccountSelector asyncAccountSelector = new AsyncAccountSelector(activity,
			new AsyncAccountSelector.AccountWaiter() {

				@Override
				public void onAccountObtained(AsyncAccountSelector.Result result,
					Account account) {

					accountSelectionResult = result;
					obtainedAccount = account;

					latch.countDown();
				}
			});

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				asyncAccountSelector.selectAccount();
			}
		});

		try {
			latch.await();
		}
		catch (InterruptedException e) {
			throw new RuntimeException();
		}

		if (accountSelectionResult == AsyncAccountSelector.Result.NO_ACCOUNTS_REGISTERED) {
			throw new NoAccountRegisteredException();
		}
		return obtainedAccount;
	}
}
