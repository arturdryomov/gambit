package app.android.gambit.ui;


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import com.google.api.services.drive.DriveScopes;


public class GoogleDriveAuthorizer
{
	private static final String DRIVE_AUTH_TOKEN_TYPE;
	static {
		DRIVE_AUTH_TOKEN_TYPE = String.format("oauth2:%s", DriveScopes.DRIVE);
	}

	private static final String ACCOUNT_TYPE = "com.google";

	private final Activity activity;

	public GoogleDriveAuthorizer(Activity activity) {
		this.activity = activity;
	}

	/**
	 * @throws AuthorizationCanceledException if user cancelled authorization.
	 * @throws AuthorizationFailedException if an error occurred during authorization.
	 */
	public String getToken(Account account) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
		Bundle authResponse;

		try {
			authResponse = accountManager.getAuthToken(account, DRIVE_AUTH_TOKEN_TYPE, null, activity,
				null, null).getResult();
		}
		catch (OperationCanceledException e) {
			throw new AuthorizationCanceledException();
		}
		catch (AuthenticatorException e) {
			throw new AuthorizationFailedException();
		}
		catch (IOException e) {
			throw new AuthorizationFailedException();
		}

		if (!authResponse.containsKey(AccountManager.KEY_AUTHTOKEN)) {
			throw new AuthorizationFailedException();
		}

		return authResponse.getString(AccountManager.KEY_AUTHTOKEN);
	}

	public void invalidateToken(String authToken) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

		accountManager.invalidateAuthToken(ACCOUNT_TYPE, authToken);
	}
}
