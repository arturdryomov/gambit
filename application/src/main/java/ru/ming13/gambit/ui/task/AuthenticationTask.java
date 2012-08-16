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

package ru.ming13.gambit.ui.task;


import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;
import ru.ming13.gambit.ui.account.AccountCreator;
import ru.ming13.gambit.ui.account.AccountSelector;
import ru.ming13.gambit.ui.account.AuthorizationCanceledException;
import ru.ming13.gambit.ui.account.AuthorizationFailedException;
import ru.ming13.gambit.ui.account.GoogleDriveAuthorizer;
import ru.ming13.gambit.ui.account.NoAccountRegisteredException;
import ru.ming13.gambit.ui.account.SignUpCanceledException;
import ru.ming13.gambit.ui.account.SignUpFailedException;


public class AuthenticationTask extends AsyncTask<Void, Void, String>
{
	public interface AuthenticationCallback
	{
		public void onSuccessAuthentication(String authToken);

		public void onCancelAuthentication();

		public void onFailedAuthentication();
	}

	private static enum Mode
	{
		WITHOUT_INVALIDATION, WITH_INVALIDATION
	}

	private static enum Result
	{
		SUCCESS, CANCEL, FAIL
	}

	private Activity activity;

	private AuthenticationCallback authenticationCallback;

	private final Mode mode;

	private Result result;

	public static AuthenticationTask newInstance(Activity activity, AuthenticationCallback authenticationCallback) {
		return new AuthenticationTask(activity, authenticationCallback, Mode.WITHOUT_INVALIDATION);
	}

	private AuthenticationTask(Activity activity, AuthenticationCallback authenticationCallback, Mode mode) {
		this.activity = activity;

		this.authenticationCallback = authenticationCallback;

		this.mode = mode;
	}

	public static AuthenticationTask newInvalidationInstance(Activity activity, AuthenticationCallback authenticationCallback) {
		return new AuthenticationTask(activity, authenticationCallback, Mode.WITH_INVALIDATION);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setAuthenticationCallback(AuthenticationCallback authenticationCallback) {
		this.authenticationCallback = authenticationCallback;
	}

	@Override
	protected String doInBackground(Void... parameters) {
		try {
			Account account = pickAccount();
			String authToken = pickAuthToken(account);

			result = Result.SUCCESS;

			return authToken;
		}
		catch (SignUpCanceledException e) {
			result = Result.CANCEL;
		}
		catch (SignUpFailedException e) {
			result = Result.FAIL;
		}
		catch (AuthorizationCanceledException e) {
			result = Result.CANCEL;
		}
		catch (AuthorizationFailedException e) {
			result = Result.FAIL;
		}

		return new String();
	}

	private Account pickAccount() {
		try {
			return AccountSelector.select(activity);
		}
		catch (NoAccountRegisteredException e) {
			return AccountCreator.create(activity);
		}
	}

	private String pickAuthToken(Account account) {
		String authToken = new String();

		switch (mode) {
			case WITHOUT_INVALIDATION:
				authToken = GoogleDriveAuthorizer.getToken(activity, account);
				break;

			case WITH_INVALIDATION:
				authToken = GoogleDriveAuthorizer.getToken(activity, account);
				GoogleDriveAuthorizer.invalidateToken(activity, authToken);
				authToken = GoogleDriveAuthorizer.getToken(activity, account);
				break;
		}

		return authToken;
	}

	@Override
	protected void onPostExecute(String authToken) {
		super.onPostExecute(authToken);

		switch (result) {
			case SUCCESS:
				authenticationCallback.onSuccessAuthentication(authToken);
				break;

			case CANCEL:
				authenticationCallback.onCancelAuthentication();
				break;

			case FAIL:
				authenticationCallback.onFailedAuthentication();
				break;

			default:
				authenticationCallback.onFailedAuthentication();
				break;
		}
	}
}
