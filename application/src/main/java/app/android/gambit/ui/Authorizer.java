package app.android.gambit.ui;


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import com.google.api.services.drive.DriveScopes;


public class Authorizer
{
	private static final String DRIVE_AUTH_TOKEN_TYPE;
	static {
		DRIVE_AUTH_TOKEN_TYPE = String.format("oauth2:%s", DriveScopes.DRIVE);
	}

	private static final String ACCOUNT_TYPE = "com.google";

	private final Activity activity;

	public static enum ServiceType
	{
		DRIVE
	}

	public Authorizer(Activity activity) {
		this.activity = activity;
	}

	/**
	 * @throws AuthorizationCanceledException if user cancelled authorization.
	 * @throws AuthorizationFailedException if an error occurred during authorization.
	 */
	public String getToken(ServiceType serviceType, Account account) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());
		Bundle authResponse;
		String authType = authTypeFromServiceType(serviceType);

		try {
			authResponse = accountManager.getAuthToken(account, authType, null, activity, null,
				null).getResult();
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

	private String authTypeFromServiceType(ServiceType serviceType) {
		switch (serviceType) {
			case DRIVE:
				return DRIVE_AUTH_TOKEN_TYPE;

			default:
				throw new RuntimeException("Unknown service type");
		}
	}

	public void invalidateToken(String authToken) {
		AccountManager accountManager = AccountManager.get(activity.getApplicationContext());

		accountManager.invalidateAuthToken(ACCOUNT_TYPE, authToken);
	}
}
